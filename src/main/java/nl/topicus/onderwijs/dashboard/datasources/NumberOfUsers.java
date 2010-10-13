package nl.topicus.onderwijs.dashboard.datasources;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

@DataSourceSettings(label = "Current users", htmlClass = "number", type = Integer.class)
public interface NumberOfUsers extends DataSource<Integer> {
}
