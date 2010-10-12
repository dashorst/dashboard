package nl.topicus.onderwijs.dashboard.modules.topicus;

import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.modules.Project;

class NumberOfUsersImpl implements NumberOfUsers {
	private final TopicusApplicationStatusProvider provider;
	private final Project project;

	public NumberOfUsersImpl(Project project,
			TopicusApplicationStatusProvider provider) {
		this.project = project;
		this.provider = provider;
	}

	@Override
	public Integer getValue() {
		TopicusApplicationStatus status = provider.getStatus(project);
		return status.getNumberOfUsers();
	}
}
