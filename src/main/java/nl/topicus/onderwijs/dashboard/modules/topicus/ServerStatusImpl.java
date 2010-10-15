package nl.topicus.onderwijs.dashboard.modules.topicus;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.ServerStatus;
import nl.topicus.onderwijs.dashboard.datatypes.DotColor;
import nl.topicus.onderwijs.dashboard.modules.Project;

class ServerStatusImpl implements ServerStatus {
	private final TopicusApplicationStatusProvider provider;
	private final Project project;

	public ServerStatusImpl(Project project,
			TopicusApplicationStatusProvider provider) {
		this.project = project;
		this.provider = provider;
	}

	@Override
	public List<DotColor> getValue() {
		TopicusApplicationStatus status = provider.getStatus(project);
		return status.getServerStatusses();
	}
}
