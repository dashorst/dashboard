package nl.topicus.onderwijs.dashboard.datasources;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

@DataSourceSettings(label = "#Offline servers", htmlClass = "number", type = Integer.class)
public interface NumberOfServersOffline extends DataSource<Integer> {
}
