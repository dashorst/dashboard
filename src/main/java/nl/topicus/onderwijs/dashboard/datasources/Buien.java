package nl.topicus.onderwijs.dashboard.datasources;

import nl.topicus.onderwijs.dashboard.datatypes.BuienRadar;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

@DataSourceSettings(label = "Buien", htmlClass = "buien", type = BuienRadar.class)
public interface Buien extends DataSource<BuienRadar> {
}
