package nl.topicus.onderwijs.dashboard.web.components.bargraph;

import nl.topicus.onderwijs.dashboard.datasources.DataSourceAnnotationReader;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

public class BarDataSet {
	private String key;
	private String label;
	private String scheme;

	public BarDataSet(Class<? extends DataSource<?>> key, String scheme) {
		this.key = key.getSimpleName();
		DataSourceSettings settings = DataSourceAnnotationReader
				.getSettings(key);
		this.label = settings.label();
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
