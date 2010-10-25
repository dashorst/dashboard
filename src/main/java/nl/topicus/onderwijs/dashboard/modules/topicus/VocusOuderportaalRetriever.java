package nl.topicus.onderwijs.dashboard.modules.topicus;

import static nl.topicus.onderwijs.dashboard.modules.topicus.RetrieverUtils.getStatuspage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import nl.topicus.onderwijs.dashboard.datasources.Alerts;
import nl.topicus.onderwijs.dashboard.datasources.ApplicationVersion;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfServers;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfServersOffline;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.datasources.ServerStatus;
import nl.topicus.onderwijs.dashboard.datasources.Uptime;
import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.datatypes.DotColor;
import nl.topicus.onderwijs.dashboard.modules.Keys;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.Repository;

import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VocusOuderportaalRetriever implements Retriever,
		TopicusApplicationStatusProvider {
	private static final Logger log = LoggerFactory
			.getLogger(VocusOuderportaalRetriever.class);

	private Map<Project, List<String>> configuration = new HashMap<Project, List<String>>();

	private Map<Project, TopicusApplicationStatus> statusses = new HashMap<Project, TopicusApplicationStatus>();

	private Map<String, Alert> oldAlerts = new HashMap<String, Alert>();

	public Map<Project, TopicusApplicationStatus> getStatusses() {
		return statusses;
	}

	public VocusOuderportaalRetriever() {
		configuration.put(Keys.ATVO_OUDERS, Arrays.asList(
				"https://start.vocuslis.nl/ouders/status",
				"https://start2.vocuslis.nl/ouders/status"));
		configuration.put(Keys.PARNASSYS_OUDERS, Arrays
				.asList("https://start.parnassys.net/ouderportaal/status/"));

		for (Project project : configuration.keySet()) {
			statusses.put(project, new TopicusApplicationStatus());
		}
	}

	@Override
	public void onConfigure(Repository repository) {
		for (Project project : configuration.keySet()) {
			repository.addDataSource(project, NumberOfUsers.class,
					new NumberOfUsersImpl(project, this));
			repository.addDataSource(project, NumberOfServers.class,
					new NumberOfServersImpl(project, this));
			repository.addDataSource(project, NumberOfServersOffline.class,
					new NumberOfServersOfflineImpl(project, this));
			repository.addDataSource(project, Uptime.class, new UptimeImpl(
					project, this));
			repository.addDataSource(project, ApplicationVersion.class,
					new ApplicationVersionImpl(project, this));
			repository.addDataSource(project, ServerStatus.class,
					new ServerStatusImpl(project, this));
			repository.addDataSource(project, Alerts.class, new AlertsImpl(
					project, this));
		}
	}

	@Override
	public TopicusApplicationStatus getStatus(Project project) {
		return statusses.get(project);
	}

	@Override
	public void refreshData() {
		HashMap<Project, TopicusApplicationStatus> newStatusses = new HashMap<Project, TopicusApplicationStatus>();
		for (Project project : configuration.keySet()) {
			TopicusApplicationStatus status = getProjectData(project);
			newStatusses.put(project, status);
		}
		statusses = newStatusses;
	}

	private TopicusApplicationStatus getProjectData(Project project) {
		List<String> urls = configuration.get(project);
		if (urls == null || urls.isEmpty()) {
			TopicusApplicationStatus status = new TopicusApplicationStatus();
			status.setVersion("n/a");
			return status;
		}
		int serverIndex = 0;
		int numberOfOfflineServers = 0;
		TopicusApplicationStatus status = new TopicusApplicationStatus();
		status.setNumberOfServers(urls.size());
		List<DotColor> serverStatusses = new ArrayList<DotColor>();
		List<Alert> alerts = new ArrayList<Alert>();
		for (String statusUrl : urls) {
			serverIndex++;
			Alert oldAlert = oldAlerts.get(statusUrl);
			try {
				StatusPageResponse statuspage = getStatuspage(statusUrl);
				if (statuspage.isOffline()) {
					numberOfOfflineServers++;
					serverStatusses.add(DotColor.RED);
					Alert alert = new Alert(oldAlert, DotColor.RED, project,
							"Server " + serverIndex + " offline");
					oldAlerts.put(statusUrl, alert);
					alerts.add(alert);
					continue;
				}
				String page = statuspage.getPageContent();

				Source source = new Source(page);

				source.fullSequentialParse();

				List<Element> tableHeaders = source
						.getAllElements(HTMLElementName.TH);
				for (Element tableHeader : tableHeaders) {
					String contents = tableHeader.getContent().toString();
					if ("Applicatie".equals(contents)) {
						// getApplicationVersion(status, tableHeader);
					} else if ("Actieve sessies|Live sessions"
							.contains(contents)) {
						getNumberOfUsers(status, tableHeader);
					} else if ("Start tijd|Start time".contains(contents)) {
						getStartTijd(status, tableHeader);
					}
				}
				serverStatusses.add(DotColor.GREEN);
				oldAlerts.put(statusUrl, null);
			} catch (Exception e) {
				serverStatusses.add(DotColor.YELLOW);
				Alert alert = new Alert(oldAlert, DotColor.YELLOW, project, e
						.getMessage());
				oldAlerts.put(statusUrl, alert);
				alerts.add(alert);
				log.warn("Could not retrieve status for '" + statusUrl + "': "
						+ e.getClass().getSimpleName() + " - "
						+ e.getLocalizedMessage());
			}
		}
		status.setAlerts(alerts);
		status.setServerStatusses(serverStatusses);
		status.setNumberOfServersOnline(status.getNumberOfServers()
				- numberOfOfflineServers);
		log.info("Application status: {}->{}", project, status);
		return status;
	}

	/*
	 * <div class="yui-u first"> <h2><span>Sessies/Requests</span></h2> <table
	 * style="width:100%;margin-left:20px;"> <colgroup><col style="width:50%"
	 * /><col style="width:50%" /></colgroup> <tr><th>Actieve
	 * sessies</th><td>422</td></tr> <tr><th>Gecreëerde
	 * sessies</th><td>90505</td></tr> <tr><th>Piek
	 * sessies</th><td>706</td></tr> <tr><th>Actieve
	 * requests</th><td>3</td></tr> </table> </div>
	 */
	private Integer getNumberOfUsers(TopicusApplicationStatus status,
			Element tableHeader) {
		Element sessiesCell = tableHeader.getParentElement().getContent()
				.getFirstElement("td");

		int currentNumberOfUsers = status.getNumberOfUsers();

		String tdContents = sessiesCell.getContent().getTextExtractor()
				.toString();
		Integer numberOfUsersOnServer = Integer.valueOf(tdContents);
		status.setNumberOfUsers(currentNumberOfUsers + numberOfUsersOnServer);
		return numberOfUsersOnServer;
	}

	/*
	 * <div class="yui-u"> <h2><span>Applicatie status</span></h2> <table
	 * style="width:100%;margin-left:20px;"> <colgroup><col style="width:50%"
	 * /><col style="width:50%" /></colgroup> <tr><th>Start tijd</th><td>4
	 * oktober 2010, 17:45</td></tr> <tr><th>Beschikbaarheid</th><td>6.1
	 * days</td></tr> <tr><th>Volgende update instellingen</th><td>N/A</td></tr>
	 * <tr><th>Status</th><td>OK</td></tr> </table> </div>
	 */

	private Date getStartTijd(TopicusApplicationStatus status,
			Element tableHeader) {
		Element starttijdCell = tableHeader.getParentElement().getContent()
				.getFirstElement("td");
		String starttijdText = starttijdCell.getContent().getTextExtractor()
				.toString();
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, hh:mm",
				new Locale("NL"));
		try {
			Date starttime = sdf.parse(starttijdText);
			Date now = new Date();
			status.setUptime(Duration.milliseconds(
					now.getTime() - starttime.getTime()).getMilliseconds());
			return starttime;
		} catch (ParseException e) {
			log.error("Unable to parse starttime " + starttijdText
					+ " according to format dd MMMM yyyy, hh:mm", e);
			return null;
		}
	}
}
