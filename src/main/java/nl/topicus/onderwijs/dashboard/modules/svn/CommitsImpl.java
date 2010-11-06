package nl.topicus.onderwijs.dashboard.modules.svn;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Commits;
import nl.topicus.onderwijs.dashboard.datatypes.Commit;
import nl.topicus.onderwijs.dashboard.keys.Key;

public class CommitsImpl implements Commits {
	private SvnService service;
	private Key key;

	public CommitsImpl(Key key, SvnService service) {
		this.key = key;
		this.service = service;
	}

	@Override
	public List<Commit> getValue() {
		return service.getCommits(key);
	}
}
