package nl.topicus.onderwijs.dashboard.modules.topicus;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.ServerAlerts;
import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.keys.Key;

public class AlertsImpl implements ServerAlerts {
	private final TopicusApplicationStatusProvider provider;
	private final Key project;

	public AlertsImpl(Key project, TopicusApplicationStatusProvider provider) {
		this.project = project;
		this.provider = provider;
	}

	@Override
	public List<Alert> getValue() {
		TopicusApplicationStatus status = provider.getStatus(project);
		return status == null ? null : status.getAlerts();
	}
}
