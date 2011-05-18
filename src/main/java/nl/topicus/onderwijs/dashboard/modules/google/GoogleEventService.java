package nl.topicus.onderwijs.dashboard.modules.google;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.topicus.onderwijs.dashboard.config.ISettings;
import nl.topicus.onderwijs.dashboard.datasources.Events;
import nl.topicus.onderwijs.dashboard.datatypes.Event;
import nl.topicus.onderwijs.dashboard.keys.AbstractCodeNameKey;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.AbstractService;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.modules.ServiceConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.extensions.When;

@Service
@ServiceConfiguration(interval = 1, unit = TimeUnit.MINUTES)
public class GoogleEventService extends AbstractService {
	private static final Logger log = LoggerFactory
			.getLogger(GoogleEventService.class);
	private static final Pattern TAG_PATTERN = Pattern.compile("#\\w*");

	private List<Event> events = new ArrayList<Event>();

	@Autowired
	public GoogleEventService(ISettings settings) {
		super(settings);
	}

	@Override
	public void onConfigure(DashboardRepository repository) {
		Map<Key, Map<String, ?>> serviceSettings = getSettings()
				.getServiceSettings(GoogleEventService.class);

		for (Map.Entry<Key, Map<String, ?>> curSettingEntry : serviceSettings
				.entrySet()) {
			repository.addDataSource(curSettingEntry.getKey(), Events.class,
					new EventsImpl(curSettingEntry.getKey(), this));

			if (curSettingEntry.getKey() instanceof AbstractCodeNameKey) {
				AbstractCodeNameKey colorkey = ((AbstractCodeNameKey) curSettingEntry
						.getKey());
				if (colorkey.getColor() == null) {
					try {
						Map<String, ?> googleSettingsForProject = curSettingEntry
								.getValue();
						String username = googleSettingsForProject.get(
								"username").toString();
						String password = googleSettingsForProject.get(
								"password").toString();
						String calendarId = googleSettingsForProject.get(
								"calendarId").toString();

						CalendarService service = new CalendarService(
								"Dashboard");
						service.setUserCredentials(username, password);

						CalendarQuery calendarQuery = new CalendarQuery(
								new URL(
										"https://www.google.com/calendar/feeds/default/owncalendars/full"));
						CalendarFeed calendarFeed = service.query(
								calendarQuery, CalendarFeed.class);
						for (CalendarEntry curCalendar : calendarFeed
								.getEntries()) {
							if (curCalendar.getId().endsWith(
									calendarId.replaceAll("@", "%40"))
									&& curCalendar.getColor() != null) {
								colorkey.setColor(curCalendar.getColor()
										.getValue());
								break;
							}
						}

					} catch (Exception e) {
						log.error("Unable to fetch color codes from google: "
								+ e.getClass().getSimpleName(), e);
					}
				}
			}
		}
	}

	@Override
	public void refreshData() {
		try {
			Map<Key, Map<String, ?>> serviceSettings = getSettings()
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

				try {
					CalendarQuery myQuery = new CalendarQuery(feedUrl);
					Calendar cal = Calendar.getInstance();
					myQuery.setMinimumStartTime(dateToGDateTime(cal.getTime()));
					cal.add(Calendar.MONTH, 3);
					myQuery.setMaximumStartTime(dateToGDateTime(cal.getTime()));
					cal.add(Calendar.MONTH, -2);
					myQuery.setMaxResults(100);
					myQuery.setIntegerCustomParameter("max-results", 100);

					// Send the request and receive the response:
					CalendarEventFeed resultFeed = service.query(myQuery,
							CalendarEventFeed.class);

					for (CalendarEventEntry eventEntry : resultFeed
							.getEntries()) {
						for (When curTime : eventEntry.getTimes()) {
							Event event = new Event();
							event.setKey(curSettingEntry.getKey());
							event.setTitle(eventEntry.getTitle().getPlainText());
							// event.setOmschrijving(entry.getPlainTextContent());
							event.setDateTime(gDateTimeToDate(curTime
									.getStartTime()));
							if (curSettingEntry.getKey() instanceof AbstractCodeNameKey)
								event.setColor(((AbstractCodeNameKey) curSettingEntry
										.getKey()).getColor());
							Matcher m = TAG_PATTERN.matcher(eventEntry
									.getPlainTextContent());
							while (m.find()) {
								String curTag = m.group();
								event.getTags().add(curTag);
								if ("#major".equals(curTag))
									event.setMajor(true);
							}
							if (event.isMajor()
									|| event.getDateTime()
											.before(cal.getTime()))
								ret.add(event);
						}
					}
				} catch (Exception e) {
					log.error("Unable to refresh data from google for "
							+ calendarId + ": " + e.getClass().getSimpleName(),
							e);
				}
			}
			events = ret;
		} catch (Exception e) {
			log.error("Unable to refresh data from google: "
					+ e.getClass().getSimpleName(), e);
		}
	}

	private Date gDateTimeToDate(DateTime dateTime) {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(dateTime.getValue());
		if (dateTime.isDateOnly()) {
			cal.add(Calendar.MILLISECOND,
					-cal.getTimeZone().getOffset(cal.getTimeInMillis()));
		}
		return cal.getTime();
	}

	private DateTime dateToGDateTime(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.MILLISECOND,
				cal.getTimeZone().getOffset(cal.getTimeInMillis()));
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
}
