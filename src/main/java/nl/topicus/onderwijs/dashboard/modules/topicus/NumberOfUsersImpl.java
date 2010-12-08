package nl.topicus.onderwijs.dashboard.modules.topicus;

import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.keys.Key;

class NumberOfUsersImpl implements NumberOfUsers {
	private final TopicusApplicationStatusProvider provider;
	private final Key project;

	public NumberOfUsersImpl(Key key, TopicusApplicationStatusProvider provider) {
		this.project = key;
		this.provider = provider;
	}

	@Override
	public Integer getValue() {
		TopicusApplicationStatus status = provider.getStatus(project);
		return status == null ? null : status.getNumberOfUsers();
	}
}
