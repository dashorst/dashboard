package nl.topicus.onderwijs.dashboard.datasources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datatypes.Issue;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;
import nl.topicus.onderwijs.dashboard.modules.KeyProperty;

@DataSourceSettings(label = "Issues", htmlClass = "issues", conversion = "issue", type = Issue.class, list = true)
@KeyProperty("key")
public interface Issues extends DataSource<List<Issue>> {
}
