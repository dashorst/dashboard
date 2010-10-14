package nl.topicus.onderwijs.dashboard.modules.hudson.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Job implements Serializable {
	private static final long serialVersionUID = 1L;
	private String description;
	private String displayName;
	private String name;
	private String url;
	private boolean buildable;
	private List<BuildReference> builds = new ArrayList<BuildReference>();
	private String color;
	private BuildReference firstBuild;
	private boolean inQueue;
	private boolean keepDependencies;
	private BuildReference lastBuild;
	private BuildReference lastStableBuild;
	private BuildReference lastSuccessfulBuild;
	private BuildReference lastFailedBuild;
	private BuildReference lastCompletedBuild;
	private BuildReference lastUnstableBuild;
	private BuildReference lastUnsuccessfulBuild;
	private int nextBuildNumber;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isBuildable() {
		return buildable;
	}

	public void setBuildable(boolean buildable) {
		this.buildable = buildable;
	}

	public List<BuildReference> getBuilds() {
		return builds;
	}

	public void setBuilds(List<BuildReference> builds) {
		this.builds = builds;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public BuildReference getFirstBuild() {
		return firstBuild;
	}

	public void setFirstBuild(BuildReference firstBuild) {
		this.firstBuild = firstBuild;
	}

	public boolean isInQueue() {
		return inQueue;
	}

	public void setInQueue(boolean inQueue) {
		this.inQueue = inQueue;
	}

	public boolean isKeepDependencies() {
		return keepDependencies;
	}

	public void setKeepDependencies(boolean keepDependencies) {
		this.keepDependencies = keepDependencies;
	}

	public BuildReference getLastBuild() {
		return lastBuild;
	}

	public void setLastBuild(BuildReference lastBuild) {
		this.lastBuild = lastBuild;
	}

	public BuildReference getLastStableBuild() {
		return lastStableBuild;
	}

	public void setLastStableBuild(BuildReference lastStableBuild) {
		this.lastStableBuild = lastStableBuild;
	}

	public BuildReference getLastSuccessfulBuild() {
		return lastSuccessfulBuild;
	}

	public void setLastSuccessfulBuild(BuildReference lastSuccessfulBuild) {
		this.lastSuccessfulBuild = lastSuccessfulBuild;
	}

	public BuildReference getLastFailedBuild() {
		return lastFailedBuild;
	}

	public void setLastFailedBuild(BuildReference lastFailedBuild) {
		this.lastFailedBuild = lastFailedBuild;
	}

	public BuildReference getLastCompletedBuild() {
		return lastCompletedBuild;
	}

	public void setLastCompletedBuild(BuildReference lastCompletedBuild) {
		this.lastCompletedBuild = lastCompletedBuild;
	}

	public BuildReference getLastUnstableBuild() {
		return lastUnstableBuild;
	}

	public void setLastUnstableBuild(BuildReference lastUnstableBuild) {
		this.lastUnstableBuild = lastUnstableBuild;
	}

	public BuildReference getLastUnsuccessfulBuild() {
		return lastUnsuccessfulBuild;
	}

	public void setLastUnsuccessfulBuild(BuildReference lastUnsuccessfulBuild) {
		this.lastUnsuccessfulBuild = lastUnsuccessfulBuild;
	}

	public int getNextBuildNumber() {
		return nextBuildNumber;
	}

	public void setNextBuildNumber(int nextBuildNumber) {
		this.nextBuildNumber = nextBuildNumber;
	}
}
