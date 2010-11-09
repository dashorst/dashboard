package nl.topicus.onderwijs.dashboard.modules.twitter;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.TwitterMentions;
import nl.topicus.onderwijs.dashboard.keys.Key;
import twitter4j.Status;

public class TwitterMentionsImpl implements TwitterMentions {
	private TwitterService service;
	private Key key;

	public TwitterMentionsImpl(Key key, TwitterService service) {
		this.key = key;
		this.service = service;
	}

	@Override
	public List<Status> getValue() {
		return service.getMentions(key);
	}
}
