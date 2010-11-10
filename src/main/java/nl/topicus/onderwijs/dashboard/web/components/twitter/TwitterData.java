package nl.topicus.onderwijs.dashboard.web.components.twitter;

import java.io.Serializable;
import java.util.List;

import twitter4j.Status;

public class TwitterData implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Status> timeline;
	private List<Status> mentions;

	public TwitterData(List<Status> timeline, List<Status> mentions) {
		this.timeline = timeline;
		this.mentions = mentions;
	}

	public List<Status> getTimeline() {
		return timeline;
	}

	public void setTimeline(List<Status> timeline) {
		this.timeline = timeline;
	}

	public List<Status> getMentions() {
		return mentions;
	}

	public void setMentions(List<Status> mentions) {
		this.mentions = mentions;
	}
}
