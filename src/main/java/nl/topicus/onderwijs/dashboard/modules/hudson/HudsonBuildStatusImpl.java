package nl.topicus.onderwijs.dashboard.modules.hudson;

import java.util.ArrayList;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.HudsonBuildStatus;
import nl.topicus.onderwijs.dashboard.datatypes.DotColor;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Build;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.BuildReference;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Job;

public class HudsonBuildStatusImpl implements HudsonBuildStatus {
	private Project project;
	private HudsonService service;

	public HudsonBuildStatusImpl(Project project, HudsonService service) {
		this.project = project;
		this.service = service;
	}

	@Override
	public List<DotColor> getValue() {
		ArrayList<DotColor> result = new ArrayList<DotColor>();
		Job job = service.getJob(project);
		for (int i = 0; i < Math.min(5, job.getBuilds().size()); i++) {
			BuildReference buildReference = job.getBuilds().get(i);
			Build build = service.getBuild(project, buildReference);
			if (!build.isBuilding()) {
				switch (build.getResult()) {
				case SUCCESS:
					result.add(DotColor.GREEN);
				case UNSTABLE:
					result.add(DotColor.YELLOW);
				case FAILURE:
					result.add(DotColor.RED);
				}
			}
		}
		return result;
	}
}
