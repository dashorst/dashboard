package nl.topicus.onderwijs.dashboard.datasources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datatypes.TwitterStatus;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

@DataSourceSettings(label = "Mentions", htmlClass = "mentions", conversion = "mentions", type = TwitterStatus.class, list = true)
public interface TwitterMentions extends DataSource<List<TwitterStatus>> {
}
