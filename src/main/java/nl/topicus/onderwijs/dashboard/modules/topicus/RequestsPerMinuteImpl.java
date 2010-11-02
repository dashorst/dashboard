package nl.topicus.onderwijs.dashboard.modules.topicus;

import nl.topicus.onderwijs.dashboard.datasources.RequestsPerMinute;
import nl.topicus.onderwijs.dashboard.keys.Key;

class RequestsPerMinuteImpl implements RequestsPerMinute {
	private final TopicusApplicationStatusProvider provider;
	private final Key project;

	public RequestsPerMinuteImpl(Key project,
			TopicusApplicationStatusProvider provider) {
		this.project = project;
		this.provider = provider;
	}

	@Override
	public Integer getValue() {
		TopicusApplicationStatus status = provider.getStatus(project);
		return status.getRequestsPerMinute();
	}
}
