package nl.topicus.onderwijs.dashboard.datasources;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

@DataSourceSettings(label = "#Servers", htmlClass = "number", type = Integer.class)
public interface NumberOfServers extends DataSource<Integer> {
}
