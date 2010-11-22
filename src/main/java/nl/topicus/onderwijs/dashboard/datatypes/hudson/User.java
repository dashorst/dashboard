package nl.topicus.onderwijs.dashboard.datatypes.hudson;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private String absoluteUrl;
	private String fullName;

	public String getAbsoluteUrl() {
		return absoluteUrl;
	}

	public void setAbsoluteUrl(String absoluteUrl) {
		this.absoluteUrl = absoluteUrl;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
