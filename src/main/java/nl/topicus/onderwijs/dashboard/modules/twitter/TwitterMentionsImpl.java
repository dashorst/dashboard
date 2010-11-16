package nl.topicus.onderwijs.dashboard.modules.twitter;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.TwitterMentions;
import nl.topicus.onderwijs.dashboard.datatypes.TwitterStatus;
import nl.topicus.onderwijs.dashboard.keys.Key;

public class TwitterMentionsImpl implements TwitterMentions {
	private TwitterService service;
	private Key key;

	public TwitterMentionsImpl(Key key, TwitterService service) {
		this.key = key;
		this.service = service;
	}

	@Override
	public List<TwitterStatus> getValue() {
		return service.getMentions(key);
	}
}
