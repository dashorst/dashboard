package nl.topicus.onderwijs.dashboard.web.components.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datatypes.Event;

public class EventData {
	private Event major;
	private List<Event> minor;

	public EventData(List<Event> events) {
		Collections.sort(events, new Comparator<Event>() {
			@Override
			public int compare(Event o1, Event o2) {
				int ret = o1.getDateTime().compareTo(o2.getDateTime());
				if (ret != 0)
					return ret;
				ret = o1.getKey().getCode().compareTo(o2.getKey().getCode());
				if (ret != 0)
					return ret;
				return o1.getTitle().compareTo(o2.getTitle());
			}
		});
		minor = new ArrayList<Event>();
		for (Event curEvent : events) {
			if (curEvent.isMajor() && major == null)
				major = curEvent;
			else
				minor.add(curEvent);
		}
	}

	public Event getMajor() {
		return major;
	}

	public void setMajor(Event major) {
		this.major = major;
	}

	public List<Event> getMinor() {
		return minor;
	}

	public void setMinor(List<Event> minor) {
		this.minor = minor;
	}
}
