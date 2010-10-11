package nl.topicus.onderwijs.dashboard.modules.hudson.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Action implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Cause> causes = new ArrayList<Cause>();
	private int failCount;
	private int skipCount;
	private int totalCount;
	private String urlName;
	private List<User> participants = new ArrayList<User>();
	private Scorecard scorecard;

	public List<Cause> getCauses() {
		return causes;
	}

	public void setCauses(List<Cause> causes) {
		this.causes = causes;
	}

	public int getFailCount() {
		return failCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public int getSkipCount() {
		return skipCount;
	}

	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String getUrlName() {
		return urlName;
	}

	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}

	public List<User> getParticipants() {
		return participants;
	}

	public void setParticipants(List<User> participants) {
		this.participants = participants;
	}

	public Scorecard getScorecard() {
		return scorecard;
	}

	public void setScorecard(Scorecard scorecard) {
		this.scorecard = scorecard;
	}
}
