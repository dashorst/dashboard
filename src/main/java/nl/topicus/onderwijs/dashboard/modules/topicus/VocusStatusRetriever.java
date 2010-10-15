package nl.topicus.onderwijs.dashboard.modules.topicus;

import static nl.topicus.onderwijs.dashboard.modules.topicus.RetrieverUtils.getStatuspage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import nl.topicus.onderwijs.dashboard.datasources.ApplicationVersion;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfServers;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfServersOffline;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.datasources.ServerStatus;
import nl.topicus.onderwijs.dashboard.datasources.Uptime;
import nl.topicus.onderwijs.dashboard.datatypes.DotColor;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.Projects;
import nl.topicus.onderwijs.dashboard.modules.Repository;

import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class VocusStatusRetriever implements Retriever,
		TopicusApplicationStatusProvider {
	private static final Logger log = LoggerFactory
			.getLogger(VocusStatusRetriever.class);

	private Map<Project, List<String>> configuration = new HashMap<Project, List<String>>();

	private Map<Project, TopicusApplicationStatus> statusses = new HashMap<Project, TopicusApplicationStatus>();

	public VocusStatusRetriever() {
		configuration.put(Projects.ATVO, Arrays.asList(
				"https://start.vocuslis.nl/app/status",
				"https://start2.vocuslis.nl/app/status",
				"https://start3.atvo.nl/app/status",
				"https://start4.atvo.nl/app/status"));
		// configuration.put(Projects.IRIS, Arrays
		// .asList("https://www.irisplus.nl/irisplus-prod/app/status"),
		// );

		for (Project project : configuration.keySet()) {
			statusses.put(project, new TopicusApplicationStatus());
		}
	}

	@Override
	public TopicusApplicationStatus getStatus(Project project) {
		return statusses.get(project);
	}

	@Override
	public void onConfigure(Repository repository) {
		for (Project project : configuration.keySet()) {
			repository.addDataSourceForProject(project, NumberOfUsers.class,
					new NumberOfUsersImpl(project, this));
			repository.addDataSourceForProject(project, NumberOfServers.class,
					new NumberOfServersImpl(project, this));
			repository.addDataSourceForProject(project,
					NumberOfServersOffline.class,
					new NumberOfServersOfflineImpl(project, this));
			repository.addDataSourceForProject(project, Uptime.class,
					new UptimeImpl(project, this));
			repository.addDataSourceForProject(project,
					ApplicationVersion.class, new ApplicationVersionImpl(
							project, this));
			repository.addDataSourceForProject(project, ServerStatus.class,
					new ServerStatusImpl(project, this));
		}
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
		int numberOfOfflineServers = 0;
		TopicusApplicationStatus status = new TopicusApplicationStatus();
		status.setNumberOfServers(urls.size());
		List<DotColor> serverStatusses = new ArrayList<DotColor>();
		for (String statusUrl : urls) {
			try {
				StatusPageResponse statuspage = getStatuspage(statusUrl);
				if (statuspage.isOffline()) {
					numberOfOfflineServers++;
					serverStatusses.add(DotColor.RED);
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
						getApplicationVersion(status, tableHeader);
					} else if ("Sessions/Requests".equals(contents)) {
						getNumberOfUsers(status, tableHeader);
					}
				}

				List<Element> tdHeaders = source
						.getAllElements(HTMLElementName.TD);
				for (Element td : tdHeaders) {
					String contents = td.getContent().toString();
					if ("Starttijd".equals(contents)) {
						getStartTime(status, td);
					}
				}
				serverStatusses.add(DotColor.GREEN);
			} catch (Exception e) {
				serverStatusses.add(DotColor.YELLOW);
				log.warn("Could not retrieve status for '" + statusUrl + "': "
						+ e.getClass().getSimpleName() + " - "
						+ e.getLocalizedMessage());
			}
		}
		status.setServerStatusses(serverStatusses);
		status.setNumberOfServersOnline(status.getNumberOfServers()
				- numberOfOfflineServers);
		log.info("Application status: {}->{}", project, status);
		return status;
	}

	private Integer getNumberOfUsers(TopicusApplicationStatus status,
			Element tableHeader) {
		Element sessiesCell = tableHeader.getParentElement().getParentElement()
				.getContent().getFirstElement("class", "value_column", true);

		int currentNumberOfUsers = status.getNumberOfUsers();

		Integer numberOfUsersOnServer = Integer.valueOf(sessiesCell
				.getContent().getTextExtractor().toString());
		status.setNumberOfUsers(currentNumberOfUsers + numberOfUsersOnServer);
		return numberOfUsersOnServer;
	}

	private void getApplicationVersion(TopicusApplicationStatus status,
			Element tableHeader) {
		Element versieCell = tableHeader.getParentElement().getParentElement()
				.getContent().getFirstElement("class", "value_column", true);
		status
				.setVersion(versieCell.getContent().getTextExtractor()
						.toString());
	}

	/*
	 * <tr><td class="name_column">Starttijd</td><td
	 * class="value_column"><span>10-10-2010 03:51:22</span></td></tr>
	 */
	private void getStartTime(TopicusApplicationStatus status, Element td) {
		Element starttijdCell = td.getParentElement().getFirstElement("class",
				"value_column", true);
		String starttijdText = starttijdCell.getTextExtractor().toString();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date starttime;
		try {
			starttime = sdf.parse(starttijdText);
			Date now = new Date();
			status.setUptime(Duration.milliseconds(
					now.getTime() - starttime.getTime()).getMilliseconds());
		} catch (ParseException e) {
			log.error("Unable to parse starttime " + starttijdText
					+ " according to format dd-MM-yyyy hh:mm:ss", e);
		}
	}

	public static void main(String[] args) {
		VocusStatusRetriever retriever = new VocusStatusRetriever();
		retriever.getProjectData(Projects.ATVO);
	}
}
