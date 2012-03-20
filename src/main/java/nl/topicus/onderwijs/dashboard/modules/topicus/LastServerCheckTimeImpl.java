package nl.topicus.onderwijs.dashboard.modules.topicus;

import java.util.Date;

import nl.topicus.onderwijs.dashboard.datasources.LastServerCheckTime;
import nl.topicus.onderwijs.dashboard.keys.Key;

class LastServerCheckTimeImpl implements LastServerCheckTime {
	private final TopicusApplicationStatusProvider provider;
	private final Key project;

	public LastServerCheckTimeImpl(Key project,
			TopicusApplicationStatusProvider provider) {
		this.project = project;
		this.provider = provider;
	}

	@Override
	public Date getValue() {
		TopicusApplicationStatus status = provider.getStatus(project);
		return status == null ? null : status.getLastCheckTime();
	}
}
