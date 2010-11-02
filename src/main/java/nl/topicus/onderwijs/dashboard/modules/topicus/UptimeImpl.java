package nl.topicus.onderwijs.dashboard.modules.topicus;

import nl.topicus.onderwijs.dashboard.datasources.Uptime;
import nl.topicus.onderwijs.dashboard.keys.Key;

import org.apache.wicket.util.time.Duration;

class UptimeImpl implements Uptime {
	private final TopicusApplicationStatusProvider provider;
	private final Key project;

	public UptimeImpl(Key project, TopicusApplicationStatusProvider provider) {
		this.project = project;
		this.provider = provider;
	}

	@Override
	public Duration getValue() {
		TopicusApplicationStatus status = provider.getStatus(project);
		return Duration.valueOf(status.getUptime());
	}
}
