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
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.Repository;

import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParnassysStatusRetriever implements
		Repository<TopicusApplicationStatus> {
	private static final Logger log = LoggerFactory
			.getLogger(ParnassysStatusRetriever.class);
	private Map<Project, List<String>> statusUrls = new HashMap<Project, List<String>>();

	public ParnassysStatusRetriever() {
		statusUrls.put(new Project("parnassys", "ParnasSys"),
				Arrays.asList("https://start.parnassys.net/bao/status.m"));

	}

	public static void main(String[] args) {
		ParnassysStatusRetriever retriever = new ParnassysStatusRetriever();
		retriever.getProjectData(new Project("parnassys", "ParnasSys"));
	}

	@Override
	public TopicusApplicationStatus getProjectData(Project project) {
		List<String> urls = statusUrls.get(project);
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
}
