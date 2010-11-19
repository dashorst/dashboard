package nl.topicus.onderwijs.dashboard.modules.topicus;

import java.util.ArrayList;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsersPerServer;
import nl.topicus.onderwijs.dashboard.keys.Key;

class NumberOfUsersPerServerImpl implements NumberOfUsersPerServer {
	private final TopicusApplicationStatusProvider provider;
	private final Key project;

	public NumberOfUsersPerServerImpl(Key key,
			TopicusApplicationStatusProvider provider) {
		this.project = key;
		this.provider = provider;
	}

	@Override
	public List<Integer> getValue() {
		TopicusApplicationStatus status = provider.getStatus(project);
		List<Integer> ret = new ArrayList<Integer>();
		for (TopicusServerStatus curServer : status.getServers()) {
			Integer curUsers = curServer.getNumberOfUsers();
			ret.add(curUsers == null ? 0 : curUsers);
		}
		return ret;
	}
}
