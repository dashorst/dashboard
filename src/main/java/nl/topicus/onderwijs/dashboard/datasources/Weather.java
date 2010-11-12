package nl.topicus.onderwijs.dashboard.datasources;

import nl.topicus.onderwijs.dashboard.datatypes.WeatherReport;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

@DataSourceSettings(label = "Weather", htmlClass = "weather", type = WeatherReport.class)
public interface Weather extends DataSource<WeatherReport> {
}
