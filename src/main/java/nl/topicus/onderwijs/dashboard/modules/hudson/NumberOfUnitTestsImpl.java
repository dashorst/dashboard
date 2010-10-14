package nl.topicus.onderwijs.dashboard.modules.hudson;

import nl.topicus.onderwijs.dashboard.datasources.NumberOfUnitTests;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Action;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Build;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.BuildReference;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Job;

public class NumberOfUnitTestsImpl implements NumberOfUnitTests {
	private Project project;
	private HudsonService service;

	public NumberOfUnitTestsImpl(Project project, HudsonService service) {
		this.project = project;
		this.service = service;
	}

	@Override
	public Integer getValue() {
		Job job = service.getJob(project);
		BuildReference lastSuccessfulBuild = job.getLastSuccessfulBuild();
		Build build = service.getBuild(project, lastSuccessfulBuild);
		if (build != null && build.getActions() != null) {
			for (Action action : build.getActions()) {
				if ("testReport".equals(action.getUrlName())) {
					return action.getTotalCount();
				}
			}
		}
		return null;
	}
}
