package nl.topicus.onderwijs.dashboard.datasources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;
import nl.topicus.onderwijs.dashboard.modules.KeyProperty;
import nl.topicus.onderwijs.dashboard.modules.ns.model.Train;

@DataSourceSettings(label = "Deventer", htmlClass = "ns", conversion = "train", type = Train.class, list = true)
@KeyProperty("key")
public interface Trains extends DataSource<List<Train>> {
}
