package nl.topicus.onderwijs.dashboard.modules.hudson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import nl.topicus.onderwijs.dashboard.datasources.HudsonBuildStatus;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUnitTests;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.Projects;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Build;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.BuildReference;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Hudson;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Job;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.JobReference;
import nl.topicus.onderwijs.dashboard.modules.topicus.Retriever;
import nl.topicus.onderwijs.dashboard.modules.topicus.RetrieverUtils;
import nl.topicus.onderwijs.dashboard.modules.topicus.StatusPageResponse;

import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HudsonService implements Retriever, BuildProvider, JobProvider {
	private static final Logger log = LoggerFactory
			.getLogger(HudsonService.class);

	private Map<Project, List<Pattern>> configuration = new HashMap<Project, List<Pattern>>();
	private final ObjectMapper mapper = new ObjectMapper();

	public HudsonService() { // WicketApplication application) {
		// this.application = application;
		mapper.getDeserializationConfig().disable(
				Feature.FAIL_ON_UNKNOWN_PROPERTIES);

		configuration.put(Projects.ATVO,
				Arrays.asList(Pattern.compile("Vocus \\- (.*)")));
		configuration.put(Projects.ATVO_OUDERS,
				Arrays.asList(Pattern.compile("Vocus Ouders")));
		configuration.put(Projects.EDUARTE,
				Arrays.asList(Pattern.compile("EduArte v(.*)")));
	}

	@Override
	public void onConfigure(Repository repository) {
		for (Project project : configuration.keySet()) {
			repository.addDataSourceForProject(project,
					NumberOfUnitTests.class, new NumberOfUnitTestsImpl(project,
							this));
			repository.addDataSourceForProject(project,
					HudsonBuildStatus.class, new HudsonBuildStatusImpl(project,
							this));
		}
	}

	public static void main(String[] args) {
		new HudsonService().refreshData();
	}

	public void refreshData() {
		try {
			StatusPageResponse response = RetrieverUtils
					.getStatuspage("http://builds.topicus.local/api/json");
			if (response.getHttpStatusCode() != 200) {
				return;
			}
			Hudson hudson = mapper.readValue(response.getPageContent(),
					Hudson.class);
			for (JobReference jobReference : hudson.getJobs()) {
				String name = jobReference.getName();
				for (Entry<Project, List<Pattern>> entry : configuration
						.entrySet()) {
					for (Pattern pattern : entry.getValue()) {
						if (pattern.matcher(name).matches()) {
							refreshData(entry.getKey(), jobReference);
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	private void refreshData(Project project, JobReference jobReference)
			throws Exception {

	}

	@Override
	public Job getJob(Project project) {
		// StatusPageResponse response =
		// RetrieverUtils.getStatuspage(jobReference
		// .getUrl() + "/api/json");
		// if (response.getHttpStatusCode() != 200) {
		// return;
		// }
		// Job job = mapper.readValue(response.getPageContent(), Job.class);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Build getBuild(Project project, BuildReference reference) {
		try {
			StatusPageResponse response = RetrieverUtils
					.getStatuspage(reference.getUrl() + "/api/json");
			if (response.getHttpStatusCode() != 200) {
				return null;
			}
			Build build = mapper.readValue(response.getPageContent(),
					Build.class);
			return build;
		} catch (Exception e) {
			log.error(
					"Unable to retrieve project " + project.getName()
							+ " build " + reference.getNumber() + " from "
							+ reference.getUrl(), e);
			return null;
		}
	}
}
