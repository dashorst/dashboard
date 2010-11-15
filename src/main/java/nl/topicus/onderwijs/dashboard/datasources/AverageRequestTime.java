package nl.topicus.onderwijs.dashboard.datasources;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

@DataSourceSettings(label = "Request time", unit = "ms", htmlClass = "number", type = Integer.class)
public interface AverageRequestTime extends DataSource<Integer> {
}
