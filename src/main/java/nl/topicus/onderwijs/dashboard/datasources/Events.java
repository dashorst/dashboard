package nl.topicus.onderwijs.dashboard.datasources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datatypes.Event;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;
import nl.topicus.onderwijs.dashboard.modules.KeyProperty;

@DataSourceSettings(label = "Events", htmlClass = "events", conversion = "event", type = Event.class, list = true)
@KeyProperty("key")
public interface Events extends DataSource<List<Event>> {
}
