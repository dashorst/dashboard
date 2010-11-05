package nl.topicus.onderwijs.dashboard.modules.mantis;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Issues;
import nl.topicus.onderwijs.dashboard.datatypes.Issue;
import nl.topicus.onderwijs.dashboard.keys.Key;

public class IssuesImpl implements Issues {
	private MantisService service;
	private Key key;

	public IssuesImpl(Key key, MantisService service) {
		this.key = key;
		this.service = service;
	}

	@Override
	public List<Issue> getValue() {
		return service.getIssues(key);
	}
}
