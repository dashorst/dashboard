package nl.topicus.onderwijs.dashboard.modules.topicus;

import static nl.topicus.onderwijs.dashboard.modules.topicus.RetrieverUtils.getStatuspage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import nl.topicus.onderwijs.dashboard.datasources.ApplicationVersion;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfServers;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfServersOffline;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsersPerServer;
import nl.topicus.onderwijs.dashboard.datasources.ServerAlerts;
import nl.topicus.onderwijs.dashboard.datasources.ServerStatus;
import nl.topicus.onderwijs.dashboard.datasources.Uptime;
import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.datatypes.DotColor;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.AbstractService;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.modules.ServiceConfiguration;

import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@ServiceConfiguration(interval = 30, unit = TimeUnit.SECONDS)
public class VocusOuderportaalRetriever extends AbstractService implements
		TopicusApplicationStatusProvider {
	private static final Logger log = LoggerFactory
			.getLogger(VocusOuderportaalRetriever.class);

	private Map<Key, TopicusApplicationStatus> statusses = new HashMap<Key, TopicusApplicationStatus>();

	private Map<String, Alert> oldAlerts = new HashMap<String, Alert>();

	public VocusOuderportaalRetriever() {
	}

	@Override
	public void onConfigure(DashboardRepository repository) {
		Map<Key, Map<String, ?>> serviceSettings = getSettings()
				.getServiceSettings(VocusOuderportaalRetriever.class);
		for (Key project : serviceSettings.keySet()) {
			statusses.put(project, new TopicusApplicationStatus());
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
			repository.addDataSource(project, ServerAlerts.class,
					new AlertsImpl(project, this));
			repository.addDataSource(project, NumberOfUsersPerServer.class,
					new NumberOfUsersPerServerImpl(project, this));
		}
	}

	@Override
	public TopicusApplicationStatus getStatus(Key project) {
		return statusses.get(project);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refreshData() {
		HashMap<Key, TopicusApplicationStatus> newStatusses = new HashMap<Key, TopicusApplicationStatus>();
		Map<Key, Map<String, ?>> serviceSettings = getSettings()
				.getServiceSettings(VocusOuderportaalRetriever.class);

		for (Map.Entry<Key, Map<String, ?>> configEntry : serviceSettings
				.entrySet()) {
			Key project = configEntry.getKey();
			Map<String, String> urls = (Map<String, String>) configEntry
					.getValue().get("urls");
			TopicusApplicationStatus status = getProjectData(project, urls);
			newStatusses.put(project, status);
		}
		statusses = newStatusses;
	}

	private TopicusApplicationStatus getProjectData(Key project,
			Map<String, String> urls) {
		TopicusApplicationStatus status = new TopicusApplicationStatus();
		if (urls == null || urls.isEmpty()) {
			return status;
		}
		List<Alert> alerts = new ArrayList<Alert>();
		for (Entry<String, String> statusUrlEntry : urls.entrySet()) {
			String statusCode = statusUrlEntry.getKey();
			String statusUrl = statusUrlEntry.getValue();
			TopicusServerStatus server = new TopicusServerStatus(statusCode,
					statusUrl);
			status.addServer(server);
			Alert oldAlert = oldAlerts.get(statusUrl);
			try {
				StatusPageResponse statuspage = getStatuspage(statusUrl);
				if (!statuspage.isOk()) {
					server.setServerStatus(DotColor.RED);
					Alert alert = new Alert(oldAlert, DotColor.RED, project,
							"Server " + statusCode + " offline with HTTP code "
									+ statuspage.getHttpStatusCode());
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
						getNumberOfUsers(server, tableHeader);
					} else if ("Start tijd|Start time".contains(contents)) {
						getStartTijd(server, tableHeader);
					}
				}
				server.setServerStatus(DotColor.GREEN);
				oldAlerts.put(statusUrl, null);
			} catch (Exception e) {
				server.setServerStatus(DotColor.YELLOW);
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
	private Integer getNumberOfUsers(TopicusServerStatus server,
			Element tableHeader) {
		Element sessiesCell = tableHeader.getParentElement().getContent()
				.getFirstElement("td");

		String tdContents = sessiesCell.getContent().getTextExtractor()
				.toString();
		Integer numberOfUsersOnServer = Integer.valueOf(tdContents);
		server.setNumberOfUsers(numberOfUsersOnServer);
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

	private Date getStartTijd(TopicusServerStatus server, Element tableHeader) {
		Element starttijdCell = tableHeader.getParentElement().getContent()
				.getFirstElement("td");
		String starttijdText = starttijdCell.getContent().getTextExtractor()
				.toString();
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, hh:mm",
				new Locale("NL"));
		try {
			Date starttime = sdf.parse(starttijdText);
			Date now = new Date();
			server.setUptime(Duration.milliseconds(
					now.getTime() - starttime.getTime()).getMilliseconds());
			return starttime;
		} catch (ParseException e) {
			log.error("Unable to parse starttime " + starttijdText
					+ " according to format dd MMMM yyyy, hh:mm", e);
			return null;
		}
	}
}
