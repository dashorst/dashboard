package nl.topicus.onderwijs.dashboard.modules.topicus;

import nl.topicus.onderwijs.dashboard.datasources.NumberOfServersOffline;
import nl.topicus.onderwijs.dashboard.modules.Project;

class NumberOfServersOfflineImpl implements NumberOfServersOffline {
	private final TopicusApplicationStatusProvider provider;
	private final Project project;

	public NumberOfServersOfflineImpl(Project project,
			TopicusApplicationStatusProvider provider) {
		this.project = project;
		this.provider = provider;
	}

	@Override
	public Integer getValue() {
		TopicusApplicationStatus status = provider.getStatus(project);
		return status.getNumberOfServers() - status.getNumberOfServersOnline();
	}
}
