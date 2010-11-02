package nl.topicus.onderwijs.dashboard.datasources;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

@DataSourceSettings(label = "Requests/min.", htmlClass = "number", type = Integer.class)
public interface RequestsPerMinute extends DataSource<Integer> {
}
