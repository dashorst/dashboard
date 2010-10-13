package nl.topicus.onderwijs.dashboard.modules.topicus;

import nl.topicus.onderwijs.dashboard.datasources.ApplicationVersion;
import nl.topicus.onderwijs.dashboard.modules.Project;

public class ApplicationVersionImpl implements ApplicationVersion {
	private final TopicusApplicationStatusProvider provider;
	private final Project project;

	public ApplicationVersionImpl(Project project,
			TopicusApplicationStatusProvider provider) {
		this.project = project;
		this.provider = provider;
	}

	@Override
	public String getValue() {
		TopicusApplicationStatus status = provider.getStatus(project);
		return status.getVersion();
	}
}
