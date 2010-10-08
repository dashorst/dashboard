package nl.topicus.onderwijs.dashboard.web.components.bargraph;


public class BarDataSet {
	private String key;
	private String label;
	private String scheme;

	public BarDataSet(String key, String label, String scheme) {
		this.key = key;
		this.label = label;
		this.scheme = scheme;
	}

	public String getKey() {
		return key;
	}

	public String getLabel() {
		return label;
	}

	public String getScheme() {
		return scheme;
	}
}
