package nl.topicus.onderwijs.dashboard.modules.hudson;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.NumberOfUnitTests;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Action;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Build;

class NumberOfUnitTestsImpl implements NumberOfUnitTests {
	private Project project;
	private HudsonService service;

	NumberOfUnitTestsImpl(Project project, HudsonService service) {
		this.project = project;
		this.service = service;
	}

	@Override
	public Integer getValue() {
		class UnitTestComparator implements Comparator<Build> {
			@Override
			public int compare(Build o1, Build o2) {
				Action test1 = getTestReport(o1);
				Action test2 = getTestReport(o2);

				if (test1 == null && test2 == null)
					return 0;
				if (test1 == null)
					return 1;
				if (test2 == null)
					return -1;
				return test1.getTotalCount() - test2.getTotalCount();
			}

		}
		List<Build> builds = service.getBuilds(project);

		if (builds.isEmpty())
			return null;

		Build buildWithMaxUnitTests = Collections.max(builds,
				new UnitTestComparator());

		Action testReport = getTestReport(buildWithMaxUnitTests);
		return testReport == null ? null : testReport.getTotalCount();
	}

	private Action getTestReport(Build build) {
		if (build != null && build.getActions() != null) {
			for (Action action : build.getActions()) {
				if ("testReport".equals(action.getUrlName())) {
					return action;
				}
			}
		}
		return null;
	}
}
