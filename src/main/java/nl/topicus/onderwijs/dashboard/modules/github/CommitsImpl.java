package nl.topicus.onderwijs.dashboard.modules.github;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Commits;
import nl.topicus.onderwijs.dashboard.datatypes.Commit;
import nl.topicus.onderwijs.dashboard.keys.Key;

public class CommitsImpl implements Commits {
	private GitHubService service;
	private Key key;

	public CommitsImpl(Key key, GitHubService service) {
		this.key = key;
		this.service = service;
	}

	@Override
	public List<Commit> getValue() {
		return service.getCommits(key);
	}
}
