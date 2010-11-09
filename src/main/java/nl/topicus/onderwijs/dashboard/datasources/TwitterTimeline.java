package nl.topicus.onderwijs.dashboard.datasources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;
import twitter4j.Status;

@DataSourceSettings(label = "Timeline", htmlClass = "timeline", conversion = "timeline", type = Status.class, list = true)
public interface TwitterTimeline extends DataSource<List<Status>> {
}
