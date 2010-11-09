package nl.topicus.onderwijs.dashboard.datasources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;
import twitter4j.Status;

@DataSourceSettings(label = "Mentions", htmlClass = "mentions", conversion = "mentions", type = Status.class, list = true)
public interface TwitterMentions extends DataSource<List<Status>> {
}
