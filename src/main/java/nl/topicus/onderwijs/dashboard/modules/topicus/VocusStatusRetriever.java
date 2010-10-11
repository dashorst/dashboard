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
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.Repository;

import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VocusStatusRetriever implements
		Repository<TopicusApplicationStatus> {
	private static final Logger log = LoggerFactory
			.getLogger(VocusStatusRetriever.class);
	private Map<Project, List<String>> statusUrls = new HashMap<Project, List<String>>();

	public VocusStatusRetriever() {
		statusUrls.put(new Project("atvo", "@VO"), Arrays.asList(
				"https://start.vocuslis.nl/app/status",
				"https://start2.vocuslis.nl/app/status",
				"https://start3.atvo.nl/app/status"));
		statusUrls.put(new Project("irisplus", "Iris+"), Arrays
				.asList("https://www.irisplus.nl/irisplus-prod/app/status"));

	}

	public static void main(String[] args) {
		VocusStatusRetriever retriever = new VocusStatusRetriever();
		retriever.getProjectData(new Project("atvo", "@VO"));
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
		status.setVersion(versieCell.getContent().getTextExtractor().toString());
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
}
