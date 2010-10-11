package nl.topicus.onderwijs.dashboard.modules.hudson.model;

import java.io.Serializable;

public class BuildReference implements Serializable {
	private static final long serialVersionUID = 1L;
	private int number;
	private String url;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
