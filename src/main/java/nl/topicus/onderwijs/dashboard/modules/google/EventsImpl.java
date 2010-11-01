package nl.topicus.onderwijs.dashboard.modules.google;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Events;
import nl.topicus.onderwijs.dashboard.datatypes.Event;
import nl.topicus.onderwijs.dashboard.modules.Key;

public class EventsImpl implements Events {
	private Key key;
	private GoogleEventService service;

	public EventsImpl(Key key, GoogleEventService service) {
		this.key = key;
		this.service = service;
	}

	@Override
	public List<Event> getValue() {
		return service.getEvents(key);
	}
}
