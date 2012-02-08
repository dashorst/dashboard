package nl.topicus.onderwijs.dashboard.datasources;

import java.util.Date;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

@DataSourceSettings(label = "Last servercheck", htmlClass = "date", type = Date.class)
public interface LastServerCheckTime extends DataSource<Date> {
}
