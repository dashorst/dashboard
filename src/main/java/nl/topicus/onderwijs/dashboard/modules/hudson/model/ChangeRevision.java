package nl.topicus.onderwijs.dashboard.modules.hudson.model;

import java.io.Serializable;

public class ChangeRevision implements Serializable {
	private static final long serialVersionUID = 1L;
	private String module;
	private int revision;

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}
}
