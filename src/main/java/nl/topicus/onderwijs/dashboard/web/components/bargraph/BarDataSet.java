package nl.topicus.onderwijs.dashboard.web.components.bargraph;

import nl.topicus.onderwijs.dashboard.modules.DataSource;

public class BarDataSet {
	private Class<? extends DataSource<?>> key;
	private String label;
	private String scheme;

	public BarDataSet(Class<? extends DataSource<?>> key, String label,
			String scheme) {
		this.key = key;
		this.label = label;
		this.scheme = scheme;
	}

	public Class<? extends DataSource<?>> getKey() {
		return key;
	}

	public String getLabel() {
		return label;
	}

	public String getScheme() {
		return scheme;
	}
}
