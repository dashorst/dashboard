package nl.topicus.onderwijs.dashboard.datasources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datatypes.Commit;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;
import nl.topicus.onderwijs.dashboard.modules.KeyProperty;

@DataSourceSettings(label = "Commits", htmlClass = "commits", conversion = "commit", type = Commit.class, list = true)
@KeyProperty("key")
public interface Commits extends DataSource<List<Commit>> {
}
