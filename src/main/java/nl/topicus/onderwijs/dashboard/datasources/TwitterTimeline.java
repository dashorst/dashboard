package nl.topicus.onderwijs.dashboard.datasources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datatypes.TwitterStatus;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;

@DataSourceSettings(label = "Timeline", htmlClass = "timeline", conversion = "timeline", type = TwitterStatus.class, list = true)
public interface TwitterTimeline extends DataSource<List<TwitterStatus>> {
}
