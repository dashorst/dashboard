package nl.topicus.onderwijs.dashboard.modules.topicus;

import static nl.topicus.onderwijs.dashboard.modules.topicus.RetrieverUtils.getStatuspage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.Repository;

public class VocusStatusRetriever implements
		Repository<TopicusApplicationStatus> {
	private Map<Project, List<String>> statusUrls = new HashMap<Project, List<String>>();

	public VocusStatusRetriever() {
		statusUrls.put(new Project("atvo", "@VO"), Arrays.asList(
				"https://start.vocuslis.nl/app/status",
				"https://start2.vocuslis.nl/app/status"));

	}

	public static void main(String[] args) {
		VocusStatusRetriever retriever = new VocusStatusRetriever();
		retriever.getProjectData(new Project("atvo", "@VO"));
	}

	@Override
	public TopicusApplicationStatus getProjectData(Project project) {
		List<String> urls = statusUrls.get(project);
		TopicusApplicationStatus status = new TopicusApplicationStatus();
		status.setNumberOfServers(urls.size());
		for (String statusUrl : urls) {
			try {
				String page = getStatuspage(statusUrl);
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Application status: ");
		System.out.println(status);
		return status;
	}

	private Integer getNumberOfUsers(TopicusApplicationStatus status,
			Element tableHeader) {
		Element sessiesCell = tableHeader.getParentElement().getParentElement()
				.getContent().getFirstElement("class", "value_column", true);

		int currentNumberOfUsers = status.getNumberOfUsers() == null ? 0
				: status.getNumberOfUsers();

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

}
