package nl.topicus.onderwijs.dashboard.modules.topicus;

import nl.topicus.onderwijs.dashboard.datasources.NumberOfServers;
import nl.topicus.onderwijs.dashboard.keys.Key;

class NumberOfServersImpl implements NumberOfServers {
	private final TopicusApplicationStatusProvider provider;
	private final Key project;

	public NumberOfServersImpl(Key project,
			TopicusApplicationStatusProvider provider) {
		this.project = project;
		this.provider = provider;
	}

	@Override
	public Integer getValue() {
		TopicusApplicationStatus status = provider.getStatus(project);
		return status.getNumberOfServers();
	}
}
