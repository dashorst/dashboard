package nl.topicus.onderwijs.dashboard.datasources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;
import nl.topicus.onderwijs.dashboard.modules.KeyProperty;

@DataSourceSettings(label = "Alerts", htmlClass = "alerts", conversion = "alert", type = Alert.class, list = true)
@KeyProperty("key")
public interface Alerts extends DataSource<List<Alert>> {
}
