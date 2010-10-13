package nl.topicus.onderwijs.dashboard.web.components.bargraph;

import nl.topicus.onderwijs.dashboard.modules.DataSource;

public class BarDataSet {
	private String key;
	private String label;
	private String scheme;

	public BarDataSet(Class<? extends DataSource<?>> key, String label,
			String scheme) {
		this.key = key.getSimpleName();
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
