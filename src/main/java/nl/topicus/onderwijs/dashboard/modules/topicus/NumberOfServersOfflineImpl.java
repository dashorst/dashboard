package nl.topicus.onderwijs.dashboard.modules.topicus;

import nl.topicus.onderwijs.dashboard.datasources.NumberOfServersOffline;
import nl.topicus.onderwijs.dashboard.keys.Key;

class NumberOfServersOfflineImpl implements NumberOfServersOffline {
	private final TopicusApplicationStatusProvider provider;
	private final Key project;

	public NumberOfServersOfflineImpl(Key project,
			TopicusApplicationStatusProvider provider) {
		this.project = project;
		this.provider = provider;
	}

	@Override
	public Integer getValue() {
		TopicusApplicationStatus status = provider.getStatus(project);
		return status == null ? null : status.getNumberOfServers()
				- status.getNumberOfServersOnline();
	}
}
