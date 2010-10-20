package nl.topicus.onderwijs.dashboard.datasources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;
import nl.topicus.onderwijs.dashboard.modules.ns.model.Train;

@DataSourceSettings(label = "Departures", htmlClass = "train", type = Train.class, list = true)
public interface Trains extends DataSource<List<Train>> {
}
