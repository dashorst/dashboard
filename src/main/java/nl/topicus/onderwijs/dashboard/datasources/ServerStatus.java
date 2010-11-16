package nl.topicus.onderwijs.dashboard.datasources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datatypes.Dot;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

@DataSourceSettings(label = "Servers", htmlClass = "dots", type = Dot.class, list = true, conversion = "dots")
public interface ServerStatus extends DataSource<List<Dot>> {
}
