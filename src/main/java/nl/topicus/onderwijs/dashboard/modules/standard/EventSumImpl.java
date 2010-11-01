package nl.topicus.onderwijs.dashboard.modules.standard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Events;
import nl.topicus.onderwijs.dashboard.datatypes.Event;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

public class EventSumImpl implements Events {
	public EventSumImpl() {
	}

	@Override
	public List<Event> getValue() {
		List<Event> ret = new ArrayList<Event>();
		Repository repository = WicketApplication.get().getRepository();
		for (Project curProject : repository.getProjects()) {
			Collection<DataSource<?>> dataSources = repository
					.getData(curProject);
			for (DataSource<?> curDataSource : dataSources) {
				if (curDataSource instanceof EventSumImpl)
					continue;
				if (curDataSource instanceof Events) {
					List<Event> newEvents = ((Events) curDataSource).getValue();
					if (newEvents != null)
						ret.addAll(newEvents);
				}
			}
		}
		Collections.sort(ret, new Comparator<Event>() {
			@Override
			public int compare(Event o1, Event o2) {
				return o1.getDateTime().compareTo(o2.getDateTime());
			}
		});
		return ret;
	}

}
