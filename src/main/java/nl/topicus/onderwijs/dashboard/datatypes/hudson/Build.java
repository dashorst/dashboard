package nl.topicus.onderwijs.dashboard.datatypes.hudson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Build implements Serializable {
	private static final long serialVersionUID = 1L;
	/** job is set by the application, and not retrieved from Hudson */
	private Job job;
	private List<Action> actions = new ArrayList<Action>();
	private boolean building;
	private String description;
	private long duration;
	private String fullDisplayName;
	private String id;
	private boolean keepLog;
	private int number;
	private Result result;
	private Date timestamp;
	private String url;
	private String builtOn;
	private ChangeSet changeSet;
	private List<User> culprits = new ArrayList<User>();

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public boolean isBuilding() {
		return building;
	}

	public void setBuilding(boolean building) {
		this.building = building;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getFullDisplayName() {
		return fullDisplayName;
	}

	public void setFullDisplayName(String fullDisplayName) {
		this.fullDisplayName = fullDisplayName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isKeepLog() {
		return keepLog;
	}

	public void setKeepLog(boolean keepLog) {
		this.keepLog = keepLog;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBuiltOn() {
		return builtOn;
	}

	public void setBuiltOn(String builtOn) {
		this.builtOn = builtOn;
	}

	public ChangeSet getChangeSet() {
		return changeSet;
	}

	public void setChangeSet(ChangeSet changeSet) {
		this.changeSet = changeSet;
	}

	public List<User> getCulprits() {
		return culprits;
	}

	public void setCulprits(List<User> culprits) {
		this.culprits = culprits;
	}
}
