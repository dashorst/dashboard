package nl.topicus.onderwijs.dashboard.modules.google;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.topicus.onderwijs.dashboard.datasources.Events;
import nl.topicus.onderwijs.dashboard.datatypes.Event;
import nl.topicus.onderwijs.dashboard.modules.Key;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.modules.Settings;
import nl.topicus.onderwijs.dashboard.modules.topicus.Retriever;
import nl.topicus.onderwijs.dashboard.persistence.config.ConfigurationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.When;

public class GoogleEventService implements Retriever {
	private static final Logger log = LoggerFactory
			.getLogger(GoogleEventService.class);
	private static final Pattern TAG_PATTERN = Pattern.compile("#\\w*");
	private List<Event> events = new ArrayList<Event>();

	@Override
	public void onConfigure(Repository repository) {
		Settings settings = getSettings();

		Map<Key, Map<String, ?>> serviceSettings = settings
				.getServiceSettings(GoogleEventService.class);
		for (Key key : serviceSettings.keySet()) {
			repository.addDataSource(key, Events.class, new EventsImpl(key,
					this));
		}
	}

	private Settings getSettings() {
		ConfigurationRepository configurationRepository = new ConfigurationRepository();
		Settings settings = configurationRepository
				.getConfiguration(Settings.class);
		return settings;
	}

	@Override
	public void refreshData() {
		try {
			Settings settings = getSettings();
			Map<Key, Map<String, ?>> serviceSettings = settings
					.getServiceSettings(GoogleEventService.class);
			List<Event> ret = new ArrayList<Event>();
			for (Map.Entry<Key, Map<String, ?>> curSettingEntry : serviceSettings
					.entrySet()) {
				Map<String, ?> googleSettingsForProject = curSettingEntry
						.getValue();
				String username = googleSettingsForProject.get("username")
						.toString();
				String password = googleSettingsForProject.get("password")
						.toString();
				String calendarId = googleSettingsForProject.get("calendarId")
						.toString();

				CalendarService service = new CalendarService("Dashboard");
				service.setUserCredentials(username, password);
				URL feedUrl = new URL("http://www.google.com/calendar/feeds/"
						+ calendarId + "/private/full");

				CalendarQuery myQuery = new CalendarQuery(feedUrl);
				Calendar cal = Calendar.getInstance();
				myQuery.setMinimumStartTime(dateToGDateTime(cal.getTime()));
				cal.add(Calendar.MONTH, 1);
				myQuery.setMaximumStartTime(dateToGDateTime(cal.getTime()));
				myQuery.setMaxResults(100);
				myQuery.setIntegerCustomParameter("max-results", 100);

				// Send the request and receive the response:
				CalendarEventFeed resultFeed;
				resultFeed = service.query(myQuery, CalendarEventFeed.class);

				for (CalendarEventEntry eventEntry : resultFeed.getEntries()) {
					for (When curTime : eventEntry.getTimes()) {
						Event event = new Event();
						event.setKey(curSettingEntry.getKey());
						event.setTitle(eventEntry.getTitle().getPlainText());
						// event.setOmschrijving(entry.getPlainTextContent());
						event.setDateTime(gDateTimeToDate(curTime
								.getStartTime()));
						Matcher m = TAG_PATTERN.matcher(eventEntry
								.getPlainTextContent());
						while (m.find()) {
							event.getTags().add(m.group());

						}
						ret.add(event);
					}
				}
			}
			events = ret;
		} catch (Exception e) {
			log.error("Unable to refresh data from google: {} {}", e.getClass()
					.getSimpleName(), e.getMessage());
		}
	}

	private Date gDateTimeToDate(DateTime dateTime) {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(dateTime.getValue());
		if (dateTime.isDateOnly()) {
			cal.add(Calendar.MILLISECOND, -cal.getTimeZone().getOffset(
					cal.getTimeInMillis()));
		}
		return cal.getTime();
	}

	private DateTime dateToGDateTime(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.MILLISECOND, cal.getTimeZone().getOffset(
				cal.getTimeInMillis()));
		return new DateTime(cal.getTime());
	}

	public List<Event> getEvents(Key key) {
		List<Event> ret = new ArrayList<Event>();
		for (Event curEvent : events) {
			if (curEvent.getKey().equals(key))
				ret.add(curEvent);
		}
		return ret;
	}

	public static void main(String[] args) {
		new GoogleEventService().refreshData();
	}
}
