package nl.topicus.onderwijs.dashboard.modules.hudson.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Scorecard implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Score> scores = new ArrayList<Score>();
	private double totalPoints;

	public List<Score> getScores() {
		return scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}

	public double getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(double totalPoints) {
		this.totalPoints = totalPoints;
	}
}
