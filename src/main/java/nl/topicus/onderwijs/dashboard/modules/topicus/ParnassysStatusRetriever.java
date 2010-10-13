package nl.topicus.onderwijs.dashboard.modules.topicus;

import static nl.topicus.onderwijs.dashboard.modules.topicus.RetrieverUtils.getStatuspage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import nl.topicus.onderwijs.dashboard.datasources.ApplicationVersion;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfServers;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfServersOffline;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.datasources.Uptime;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.Repository;

import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ParnassysStatusRetriever implements Retriever,
		TopicusApplicationStatusProvider {
	private static final Logger log = LoggerFactory
			.getLogger(ParnassysStatusRetriever.class);
	private Map<Project, List<String>> configuration = new HashMap<Project, List<String>>();

	private HashMap<Project, TopicusApplicationStatus> statusses = new HashMap<Project, TopicusApplicationStatus>();

	public ParnassysStatusRetriever() {
		configuration.put(new Project("parnassys", "ParnasSys"),
				Arrays.asList("https://start.parnassys.net/bao/status.m"));

		for (Project project : configuration.keySet()) {
			statusses.put(project, new TopicusApplicationStatus());
		}
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
		for (String statusUrl : urls) {
			try {
				StatusPageResponse statuspage = getStatuspage(statusUrl);
				if (statuspage.isOffline()) {
					numberOfOfflineServers++;
					continue;
				}
				String page = statuspage.getPageContent();

				Source source = new Source(page);

				source.fullSequentialParse();

				List<Element> tableHeaders = source.getAllElements("class",
						"main_label", true);
				for (Element tableHeader : tableHeaders) {
					String contents = tableHeader.getContent().toString();
					if ("Actieve sessies:".equals(contents)) {
						getNumberOfUsers(status, tableHeader);
					} else if ("Gestart op:".equals(contents)) {
						getStartTijd(status, tableHeader);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		status.setNumberOfServersOnline(status.getNumberOfServers()
				- numberOfOfflineServers);
		log.info("Application status: {}->{}", project, status);
		return status;
	}

	private Integer getNumberOfUsers(TopicusApplicationStatus status,
			Element tableHeader) {
		Element sessiesCell = tableHeader.getParentElement().getContent()
				.getChildElements().get(1);

		int currentNumberOfUsers = status.getNumberOfUsers();

		String tdContents = sessiesCell.getTextExtractor().toString();
		Integer numberOfUsersOnServer = Integer.valueOf(tdContents);
		status.setNumberOfUsers(currentNumberOfUsers + numberOfUsersOnServer);
		return numberOfUsersOnServer;
	}

	private Date getStartTijd(TopicusApplicationStatus status,
			Element tableHeader) {
		Element starttijdCell = tableHeader.getParentElement().getContent()
				.getChildElements().get(1);
		String starttijdText = starttijdCell.getTextExtractor().toString();

		SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
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

	@Override
	public TopicusApplicationStatus getStatus(Project project) {
		return statusses.get(project);
	}

	public static void main(String[] args) {
		ParnassysStatusRetriever retriever = new ParnassysStatusRetriever();
		retriever.getProjectData(new Project("parnassys", "ParnasSys"));
	}
}