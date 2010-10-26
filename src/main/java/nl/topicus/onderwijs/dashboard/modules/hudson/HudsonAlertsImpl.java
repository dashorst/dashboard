package nl.topicus.onderwijs.dashboard.modules.hudson;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.HudsonAlerts;
import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.modules.Project;

class HudsonAlertsImpl implements HudsonAlerts {
	private Project project;
	private HudsonService service;

	HudsonAlertsImpl(Project project, HudsonService service) {
		this.project = project;
		this.service = service;
	}

	@Override
	public List<Alert> getValue() {
		return service.getAlerts(project);
	}
}
