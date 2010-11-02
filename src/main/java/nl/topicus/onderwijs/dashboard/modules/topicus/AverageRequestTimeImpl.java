package nl.topicus.onderwijs.dashboard.modules.topicus;

import nl.topicus.onderwijs.dashboard.datasources.AverageRequestTime;
import nl.topicus.onderwijs.dashboard.modules.Project;

class AverageRequestTimeImpl implements AverageRequestTime {
	private final TopicusApplicationStatusProvider provider;
	private final Project project;

	public AverageRequestTimeImpl(Project project,
			TopicusApplicationStatusProvider provider) {
		this.project = project;
		this.provider = provider;
	}

	@Override
	public Integer getValue() {
		TopicusApplicationStatus status = provider.getStatus(project);
		return status.getAverageRequestDuration();
	}
}
