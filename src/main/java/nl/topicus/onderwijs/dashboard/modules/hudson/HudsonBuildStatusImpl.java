package nl.topicus.onderwijs.dashboard.modules.hudson;

import java.util.ArrayList;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.HudsonBuildStatus;
import nl.topicus.onderwijs.dashboard.datatypes.Dot;
import nl.topicus.onderwijs.dashboard.datatypes.DotColor;
import nl.topicus.onderwijs.dashboard.datatypes.hudson.Build;
import nl.topicus.onderwijs.dashboard.keys.Project;

class HudsonBuildStatusImpl implements HudsonBuildStatus {
	private Project project;
	private HudsonService service;

	HudsonBuildStatusImpl(Project project, HudsonService service) {
		this.project = project;
		this.service = service;
	}

	@Override
	public List<Dot> getValue() {
		List<Build> builds = service.getBuilds(project);

		ArrayList<Dot> result = new ArrayList<Dot>();
		for (int i = 0; i < Math.min(5, builds.size()); i++) {
			Build build = builds.get(i);
			if (build.isBuilding()) {
				result.add(new Dot(DotColor.GRAY, build.getJob().getCode()));
			} else {
				switch (build.getResult()) {
				case SUCCESS:
					result
							.add(new Dot(DotColor.GREEN, build.getJob()
									.getCode()));
					break;
				case UNSTABLE:
					result.add(new Dot(DotColor.YELLOW, build.getJob()
							.getCode()));
					break;
				case FAILURE:
					result.add(new Dot(DotColor.RED, build.getJob().getCode()));
					break;
				}
			}
		}
		return result;
	}
}
