package nl.topicus.onderwijs.dashboard.datasources;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

@DataSourceSettings(label = "Version", htmlClass = "text", type = String.class)
public interface ApplicationVersion extends DataSource<String> {
}
