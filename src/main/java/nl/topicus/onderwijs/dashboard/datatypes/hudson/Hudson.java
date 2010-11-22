package nl.topicus.onderwijs.dashboard.datatypes.hudson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Hudson implements Serializable {
	private static final long serialVersionUID = 1L;
	private String mode;
	private String nodeDescription;
	private String nodeName;
	private int numExecutors;
	private String description;
	private List<JobReference> jobs = new ArrayList<JobReference>();

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getNodeDescription() {
		return nodeDescription;
	}

	public void setNodeDescription(String nodeDescription) {
		this.nodeDescription = nodeDescription;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public int getNumExecutors() {
		return numExecutors;
	}

	public void setNumExecutors(int numExecutors) {
		this.numExecutors = numExecutors;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<JobReference> getJobs() {
		return jobs;
	}

	public void setJobs(List<JobReference> jobs) {
		this.jobs = jobs;
	}
}
