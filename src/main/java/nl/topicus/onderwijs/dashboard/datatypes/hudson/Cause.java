package nl.topicus.onderwijs.dashboard.datatypes.hudson;

import java.io.Serializable;

public class Cause implements Serializable {
	private static final long serialVersionUID = 1L;
	private String shortDescription;

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
}
