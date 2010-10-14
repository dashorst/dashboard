package nl.topicus.onderwijs.dashboard.modules.hudson;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.HudsonBuildNumber;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Job;

class HudsonBuildNumberImpl implements HudsonBuildNumber {
	private Project project;
	private HudsonService service;

	HudsonBuildNumberImpl(Project project, HudsonService service) {
		this.project = project;
		this.service = service;
	}

	@Override
	public Integer getValue() {
		class BuildNumberComparator implements Comparator<Job> {
			@Override
			public int compare(Job o1, Job o2) {
				return o1.getNextBuildNumber() - o2.getNextBuildNumber();
			}
		}
		List<Job> jobs = service.getJobs(project);
		if (jobs.isEmpty())
			return null;
		Job jobWithHighestNextBuildNumber = Collections.max(jobs,
				new BuildNumberComparator());
		return jobWithHighestNextBuildNumber.getNextBuildNumber() - 1;
	}
}
