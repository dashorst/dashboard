package nl.topicus.onderwijs.dashboard.web.components.twitter;

import java.io.Serializable;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datatypes.TwitterStatus;

public class TwitterData implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<TwitterStatus> timeline;
	private List<TwitterStatus> mentions;

	public TwitterData(List<TwitterStatus> timeline,
			List<TwitterStatus> mentions) {
		this.timeline = timeline;
		this.mentions = mentions;
	}

	public List<TwitterStatus> getTimeline() {
		return timeline;
	}

	public void setTimeline(List<TwitterStatus> timeline) {
		this.timeline = timeline;
	}

	public List<TwitterStatus> getMentions() {
		return mentions;
	}

	public void setMentions(List<TwitterStatus> mentions) {
		this.mentions = mentions;
	}
}
