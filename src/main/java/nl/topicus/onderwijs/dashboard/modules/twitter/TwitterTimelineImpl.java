package nl.topicus.onderwijs.dashboard.modules.twitter;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.TwitterTimeline;
import nl.topicus.onderwijs.dashboard.datatypes.TwitterStatus;
import nl.topicus.onderwijs.dashboard.keys.Key;

public class TwitterTimelineImpl implements TwitterTimeline {
	private TwitterService service;
	private Key key;

	public TwitterTimelineImpl(Key key, TwitterService service) {
		this.key = key;
		this.service = service;
	}

	@Override
	public List<TwitterStatus> getValue() {
		return service.getTimeline(key);
	}
}
