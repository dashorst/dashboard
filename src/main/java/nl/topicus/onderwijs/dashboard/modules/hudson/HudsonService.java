package nl.topicus.onderwijs.dashboard.modules.hudson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import nl.topicus.onderwijs.dashboard.datasources.HudsonAlerts;
import nl.topicus.onderwijs.dashboard.datasources.HudsonBuildNumber;
import nl.topicus.onderwijs.dashboard.datasources.HudsonBuildStatus;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUnitTests;
import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.datatypes.DotColor;
import nl.topicus.onderwijs.dashboard.modules.Key;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.modules.Settings;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Build;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.BuildReference;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Hudson;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Job;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.JobReference;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Result;
import nl.topicus.onderwijs.dashboard.modules.topicus.Retriever;
import nl.topicus.onderwijs.dashboard.modules.topicus.RetrieverUtils;
import nl.topicus.onderwijs.dashboard.modules.topicus.StatusPageResponse;
import nl.topicus.onderwijs.dashboard.persistence.config.ConfigurationRepository;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HudsonService implements Retriever {
	private static final Logger log = LoggerFactory
			.getLogger(HudsonService.class);

	private final ObjectMapper mapper = new ObjectMapper();

	private Map<HudsonKey<BuildReference>, Build> buildsCache = new HashMap<HudsonKey<BuildReference>, Build>();
	private ConcurrentHashMap<Project, List<Job>> jobsCache = new ConcurrentHashMap<Project, List<Job>>();
	private ConcurrentHashMap<String, Alert> alertsCache = new ConcurrentHashMap<String, Alert>();

	public HudsonService() {
		mapper.getDeserializationConfig().disable(
				Feature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	@Override
	public void onConfigure(Repository repository) {
		Settings settings = getSettings();

		Map<Key, Map<String, ?>> serviceSettings = settings
				.getServiceSettings(HudsonService.class);
		for (Key key : serviceSettings.keySet()) {
			if (key instanceof Project) {
				Project project = (Project) key;
				repository.addDataSource(project, NumberOfUnitTests.class,
						new NumberOfUnitTestsImpl(project, this));
				repository.addDataSource(project, HudsonBuildStatus.class,
						new HudsonBuildStatusImpl(project, this));
				repository.addDataSource(project, HudsonBuildNumber.class,
						new HudsonBuildNumberImpl(project, this));
				repository.addDataSource(project, HudsonAlerts.class,
						new HudsonAlertsImpl(project, this));
			}
		}
	}

	private Settings getSettings() {
		ConfigurationRepository configurationRepository = new ConfigurationRepository();
		Settings settings = configurationRepository
				.getConfiguration(Settings.class);
		return settings;
	}

	public static void main(String[] args) {
		new HudsonService().refreshData();
	}

	public void refreshData() {
		try {
			Settings settings = getSettings();

			Map<Key, Map<String, ?>> serviceSettings = settings
					.getServiceSettings(HudsonService.class);
			for (Entry<Key, Map<String, ?>> entry : serviceSettings.entrySet()) {
				if (!(entry.getKey() instanceof Project))
					continue;

				Project project = (Project) entry.getKey();

				Map<String, ?> hudsonSettingsForProject = entry.getValue();

				String url = hudsonSettingsForProject.get("url").toString();

				@SuppressWarnings("unchecked")
				Collection<String> patterns = (Collection<String>) hudsonSettingsForProject
						.get("matchers");

				if (!url.endsWith("/"))
					url = url + "/";
				StatusPageResponse response = RetrieverUtils.getStatuspage(url
						+ "api/json");
				if (response.getHttpStatusCode() != 200) {
					return;
				}
				Hudson hudson = mapper.readValue(response.getPageContent(),
						Hudson.class);

				for (JobReference jobReference : hudson.getJobs()) {
					String name = jobReference.getName();
					for (String pattern : patterns) {
						if (Pattern.matches(pattern, name)) {
							refreshData(project, jobReference);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Unable to refresh data from hudson: {} {}", e.getClass()
					.getSimpleName(), e.getMessage());
		}
	}

	private void refreshData(Project project, JobReference jobReference)
			throws Exception {
		StatusPageResponse response = RetrieverUtils.getStatuspage(jobReference
				.getUrl()
				+ "api/json");
		if (response.getHttpStatusCode() != 200) {
			return;
		}
		Job job = mapper.readValue(response.getPageContent(), Job.class);

		List<Job> jobs = jobsCache.putIfAbsent(project, new ArrayList<Job>());
		if (jobs == null)
			jobs = jobsCache.get(project);

		for (Job job2 : jobs) {
			if (job2.getName().equals(job.getName())) {
				jobs.remove(job2);
				break;
			}
		}
		jobs.add(job);
		jobsCache.put(project, jobs);
	}

	public List<Job> getJobs(Project project) {
		List<Job> list = jobsCache.get(project);
		return list == null ? new ArrayList<Job>() : new ArrayList<Job>(list);
	}

	public List<Build> getBuilds(Project project) {
		List<Job> jobs = getJobs(project);

		List<Build> builds = new ArrayList<Build>();
		for (Job job : jobs) {
			for (int i = 0; i < Math.min(5, job.getBuilds().size()); i++) {
				BuildReference buildReference = job.getBuilds().get(i);
				Build build = getBuild(project, buildReference);
				builds.add(build);
			}
		}
		Collections.sort(builds, new BuildsComparator());
		return builds;
	}

	public Build getBuild(Project project, BuildReference reference) {
		if (buildsCache.containsKey(HudsonKey.of(project, reference))) {
			return buildsCache.get(HudsonKey.of(project, reference));
		}
		if (buildsCache.size() > 1000) {
			buildsCache.clear();
		}
		try {
			StatusPageResponse response = RetrieverUtils
					.getStatuspage(reference.getUrl() + "api/json");
			if (response.getHttpStatusCode() != 200) {
				return null;
			}
			Build build = mapper.readValue(response.getPageContent(),
					Build.class);
			if (!build.isBuilding()) {
				// don't store the build result in the cache when it's still
				// building.
				buildsCache.put(HudsonKey.of(project, reference), build);
			}
			return build;
		} catch (Exception e) {
			log.error("Unable to retrieve project " + project.getName()
					+ " build " + reference.getNumber() + " from "
					+ reference.getUrl(), e);
			return null;
		}
	}

	public List<Alert> getAlerts(Project project) {
		List<Job> jobs = getJobs(project);

		Map<String, Build> builds = new HashMap<String, Build>();
		for (Job job : jobs) {
			for (int i = 0; i < Math.min(1, job.getBuilds().size()); i++) {
				BuildReference buildReference = job.getBuilds().get(i);
				Build build = getBuild(project, buildReference);
				builds.put(job.getName(), build);
			}
		}

		List<Alert> ret = new ArrayList<Alert>();
		for (Job curJob : jobs) {
			Build curBuild = builds.get(curJob.getName());
			if (curBuild == null)
				continue;

			if (Result.UNSTABLE.equals(curBuild.getResult())) {
				Alert alert = new Alert(alertsCache.get(curJob.getName()),
						DotColor.YELLOW, project, "Build "
								+ curBuild.getNumber() + " is unstable");
				alertsCache.put(curJob.getName(), alert);
				ret.add(alert);
			} else if (Result.FAILURE.equals(curBuild.getResult())) {
				Alert alert = new Alert(alertsCache.get(curJob.getName()),
						DotColor.RED, project, "Build " + curBuild.getNumber()
								+ " failed");
				alertsCache.put(curJob.getName(), alert);
				ret.add(alert);
			} else
				alertsCache.remove(curJob.getName());
		}
		return ret;
	}

	public static class BuildsComparator implements Comparator<Build> {
		@Override
		public int compare(Build o1, Build o2) {
			return o2.getTimestamp().compareTo(o1.getTimestamp());
		}
	}

	public static class HudsonKey<T> {
		private final Project project;
		private final T reference;

		public HudsonKey(Project project, T reference) {
			this.project = project;
			this.reference = reference;
		}

		public Project getProject() {
			return project;
		}

		public T getReference() {
			return reference;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((project == null) ? 0 : project.hashCode());
			result = prime * result
					+ ((reference == null) ? 0 : reference.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			HudsonKey<?> other = (HudsonKey<?>) obj;
			if (project == null) {
				if (other.project != null)
					return false;
			} else if (!project.equals(other.project))
				return false;
			if (reference == null) {
				if (other.reference != null)
					return false;
			} else if (!reference.equals(other.reference))
				return false;
			return true;
		}

		static <R> HudsonKey<R> of(Project p, R reference) {
			return new HudsonKey<R>(p, reference);
		}
	}
}
