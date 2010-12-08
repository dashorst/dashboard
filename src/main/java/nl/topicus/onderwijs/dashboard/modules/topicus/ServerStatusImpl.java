package nl.topicus.onderwijs.dashboard.modules.topicus;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.ServerStatus;
import nl.topicus.onderwijs.dashboard.datatypes.Dot;
import nl.topicus.onderwijs.dashboard.keys.Key;

class ServerStatusImpl implements ServerStatus {
	private final TopicusApplicationStatusProvider provider;
	private final Key project;

	public ServerStatusImpl(Key project,
			TopicusApplicationStatusProvider provider) {
		this.project = project;
		this.provider = provider;
	}

	@Override
	public List<Dot> getValue() {
		TopicusApplicationStatus status = provider.getStatus(project);
		return status == null ? null : status.getServerStatusses();
	}
}
