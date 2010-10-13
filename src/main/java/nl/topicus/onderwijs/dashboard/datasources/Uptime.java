package nl.topicus.onderwijs.dashboard.datasources;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

import org.apache.wicket.util.time.Duration;

@DataSourceSettings(label = "Uptime", htmlClass = "text", type = Duration.class)
public interface Uptime extends DataSource<Duration> {
}
