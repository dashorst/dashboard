package nl.topicus.onderwijs.dashboard.modules.hudson;

import java.util.ArrayList;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.HudsonBuildStatus;
import nl.topicus.onderwijs.dashboard.datatypes.DotColor;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Build;

class HudsonBuildStatusImpl implements HudsonBuildStatus {
	private Project project;
	private HudsonService service;

	HudsonBuildStatusImpl(Project project, HudsonService service) {
		this.project = project;
		this.service = service;
	}

	@Override
	public List<DotColor> getValue() {
		List<Build> builds = service.getBuilds(project);

		ArrayList<DotColor> result = new ArrayList<DotColor>();
		for (int i = 0; i < Math.min(5, builds.size()); i++) {
			Build build = builds.get(i);
			if (build.isBuilding()) {
				result.add(DotColor.GRAY);
			} else {
				switch (build.getResult()) {
				case SUCCESS:
					result.add(DotColor.GREEN);
					break;
				case UNSTABLE:
					result.add(DotColor.YELLOW);
					break;
				case FAILURE:
					result.add(DotColor.RED);
					break;
				}
			}
		}
		// DotColor min = Collections.min(result);
		// result.add(0, min);
		return result;
	}
}
