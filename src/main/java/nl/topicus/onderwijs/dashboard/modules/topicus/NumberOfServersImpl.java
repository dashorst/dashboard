package nl.topicus.onderwijs.dashboard.modules.topicus;

import nl.topicus.onderwijs.dashboard.datasources.NumberOfServers;
import nl.topicus.onderwijs.dashboard.modules.Project;

class NumberOfServersImpl implements NumberOfServers {
	private final TopicusApplicationStatusProvider provider;
	private final Project project;

	public NumberOfServersImpl(Project project,
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
