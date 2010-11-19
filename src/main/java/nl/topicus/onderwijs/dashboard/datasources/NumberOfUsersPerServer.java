package nl.topicus.onderwijs.dashboard.datasources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

@DataSourceSettings(label = "Users/server", htmlClass = "minibargraph", conversion = "bargraph", type = Integer.class, list = true)
public interface NumberOfUsersPerServer extends DataSource<List<Integer>> {
}
