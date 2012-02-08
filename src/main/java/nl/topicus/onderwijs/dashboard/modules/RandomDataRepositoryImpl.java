package nl.topicus.onderwijs.dashboard.modules;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import nl.topicus.onderwijs.dashboard.datasources.Alerts;
import nl.topicus.onderwijs.dashboard.datasources.AverageRequestTime;
import nl.topicus.onderwijs.dashboard.datasources.DataSourceAnnotationReader;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.datatypes.Commit;
import nl.topicus.onderwijs.dashboard.datatypes.Dot;
import nl.topicus.onderwijs.dashboard.datatypes.DotColor;
import nl.topicus.onderwijs.dashboard.datatypes.Event;
import nl.topicus.onderwijs.dashboard.datatypes.Issue;
import nl.topicus.onderwijs.dashboard.datatypes.IssuePriority;
import nl.topicus.onderwijs.dashboard.datatypes.IssueSeverity;
import nl.topicus.onderwijs.dashboard.datatypes.IssueStatus;
import nl.topicus.onderwijs.dashboard.datatypes.TwitterStatus;
import nl.topicus.onderwijs.dashboard.datatypes.WeatherReport;
import nl.topicus.onderwijs.dashboard.datatypes.WeatherType;
import nl.topicus.onderwijs.dashboard.datatypes.train.Train;
import nl.topicus.onderwijs.dashboard.datatypes.train.TrainType;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.keys.Project;
import nl.topicus.onderwijs.dashboard.keys.Summary;
import nl.topicus.onderwijs.dashboard.modules.wettercom.WetterComService;

import org.apache.wicket.util.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("random")
public class RandomDataRepositoryImpl extends TimerTask implements
		DashboardRepository {
	private static final long HEX_10_DIGITS = 256L * 256L * 256L * 256L * 256L;

	private static final Object NULL = new Object();

	@Autowired
	@Resource(name = "online")
	private DashboardRepository base;
	private Set<Class<? extends DataSource<?>>> sources = new HashSet<Class<? extends DataSource<?>>>();
	private ConcurrentHashMap<String, Object> dataCache = new ConcurrentHashMap<String, Object>();
	private Map<Key, List<Event>> eventCache = new HashMap<Key, List<Event>>();
	private Timer timer;

	public RandomDataRepositoryImpl() {
		start();
	}

	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void start() {
		if (timer == null) {
			timer = new Timer("Random Data Updater", true);
			timer.scheduleAtFixedRate(this, 0, Duration.seconds(5)
					.getMilliseconds());
		}
	}

	public <T extends DataSource<?>> void addDataSource(Key key,
			Class<T> datasourceType, T dataSource) {
		sources.add(datasourceType);
		base.addDataSource(key, datasourceType, dataSource);
	}

	public List<Project> getProjects() {
		return base.getProjects();
	}

	public <T extends Key> List<T> getKeys(Class<T> keyType) {
		return base.getKeys(keyType);
	}

	public Collection<DataSource<?>> getData(Key key) {
		Collection<DataSource<?>> ret = new ArrayList<DataSource<?>>();
		for (Class<? extends DataSource<?>> curDataSource : sources)
			ret.add(createDataSource(key, curDataSource));
		return ret;
	}

	@Override
	public <T extends DataSource<?>> Map<Key, T> getData(Class<T> datasource) {
		Map<Key, T> ret = new HashMap<Key, T>();
		for (Key curKey : getKeys(Key.class)) {
			T dataSource = createDataSource(curKey, datasource);
			if (dataSource != null)
				ret.put(curKey, dataSource);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private <T extends DataSource<?>> T createDataSource(final Key key,
			final Class<T> dataSource) {
		// summary is a special case, use the original datasource
		if (key.equals(Summary.get()))
			return base.getData(dataSource).get(key);
		// summary is a special case, use the original datasource
		if (Alerts.class.isAssignableFrom(dataSource)
				&& !(key instanceof Project))
			return null;

		final DataSourceSettings settings = DataSourceAnnotationReader
				.getSettings(dataSource);
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				Random random = new Random();
				if (method.getName().equals("getValue")) {
					String dataKey = key.getCode() + "-" + dataSource.getName();
					Object value;
					if (settings.type().equals(Integer.class)) {
						if (settings.list()) {
							List<Integer> ret = new ArrayList<Integer>();
							for (int count = 0; count < 5; count++)
								ret.add(random.nextInt(200));
							value = ret;
						} else {
							value = createInteger(key, dataSource, random);
						}
					} else if (settings.type().equals(Duration.class))
						value = Duration
								.milliseconds(Math.round(Math.random() * 100000000));
					else if (settings.type().equals(String.class))
						value = "random";
					else if (settings.type().equals(Date.class)) {
						value = new Date();
					} else if (settings.type().equals(WeatherReport.class)) {
						value = createRandomWeather();
					} else if (settings.type().equals(Dot.class)
							&& settings.list()) {
						List<Dot> ret = new ArrayList<Dot>();
						for (int count = 0; count < 5; count++) {
							ret.add(new Dot(
									DotColor.values()[random.nextInt(4)],
									Integer.toString(count + 1)));
						}
						value = ret;
					} else if (settings.type().equals(Train.class)
							&& settings.list()) {
						value = createRandomTrains();
					} else if (settings.type().equals(Alert.class)
							&& settings.list()) {
						value = createRandomAlerts(key);
					} else if (settings.type().equals(Commit.class)
							&& settings.list()) {
						value = createRandomCommits(key);
					} else if (settings.type().equals(Issue.class)
							&& settings.list()) {
						value = createRandomIssues(key);
					} else if (settings.type().equals(Event.class)
							&& settings.list()) {
						value = createRandomEvents(key);
					} else if (settings.type().equals(TwitterStatus.class)
							&& settings.list()) {
						value = createRandomTweets(key);
					} else
						throw new IllegalStateException("Unsupported type "
								+ settings.type());
					Object ret = dataCache.putIfAbsent(dataKey, value);
					ret = ret == null ? value : ret;
					return ret == NULL ? null : ret;
				} else if (method.getName().equals("toString")) {
					String dataKey = key.getCode() + "-" + dataSource.getName();
					return dataKey;
				}
				throw new UnsupportedOperationException();
			}

			private Object createInteger(Key key,
					Class<? extends DataSource<?>> dataSource, Random random) {
				if (dataSource.equals(NumberOfUsers.class)) {
					if (key.getCode().equals("iris")
							|| key.getCode().equals("atvo"))
						return NULL;
				} else if (dataSource.equals(AverageRequestTime.class)) {
					if (key.getCode().equals("iris")
							|| key.getCode().equals("atvo_ouders")
							|| key.getCode().equals("parnassys_ouders"))
						return NULL;
				}
				return random.nextInt(1000);
			}

			private WeatherReport createRandomWeather() {
				Random random = new Random();
				WeatherReport ret = new WeatherReport();
				ret.setMaxTemperature(Math.round(random.nextDouble() * 500.0 - 150.0) / 10.0);
				ret.setMinTemperature(Math.round(ret.getMaxTemperature() * 10.0
						- random.nextDouble() * 100.0) / 10.0);
				ret.setRainfallProbability(random.nextInt(100));
				ret.setType(WeatherType.values()[random.nextInt(WeatherType
						.values().length)]);
				ret.setWindDirection(random.nextInt(360));
				ret.setWindSpeed(Math.round(random.nextDouble() * 1000.0) / 10.0);
				double lat = 52.25;
				double lon = 6.2;
				ret.setSunrise(WetterComService.getSunrize(lat, lon));
				ret.setSunset(WetterComService.getSunset(lat, lon));
				return ret;
			}

			private List<Train> createRandomTrains() {
				Random random = new Random();
				List<Train> ret = new ArrayList<Train>();
				for (int count = 0; count < 10; count++) {
					Train train = new Train();
					train.setType(TrainType.values()[random.nextInt(TrainType
							.values().length)]);
					train.setDestination("random random random centraal");
					int minute = random.nextInt(60);
					train.setDepartureTime(random.nextInt(24) + ":"
							+ (minute < 10 ? "0" : "") + minute);
					train.setDelay(random.nextInt(10));
					train.setPlatform(Integer.toString(random.nextInt(10)));
					ret.add(train);
				}
				Collections.sort(ret, new Comparator<Train>() {
					@Override
					public int compare(Train o1, Train o2) {
						return o1.getKey().compareTo(o2.getKey());
					}
				});
				return ret;
			}

			private List<Alert> createRandomAlerts(Key key) {
				if (!(key instanceof Project))
					return null;

				Random random = new Random();
				List<Alert> ret = new ArrayList<Alert>();
				for (int count = 0; count < random.nextInt(3); count++) {
					Alert alert = new Alert();
					alert.setProject(key);
					alert.setOverlayVisible(false);
					alert.setColor(DotColor.values()[random.nextInt(3)]);
					int minute = random.nextInt(60);
					alert.setTime(random.nextInt(24) + ":"
							+ (minute < 10 ? "0" : "") + minute);
					alert.setMessage("random exception with long message");
					ret.add(alert);
				}
				Collections.sort(ret, new Comparator<Alert>() {
					@Override
					public int compare(Alert o1, Alert o2) {
						return o1.getKey().compareTo(o2.getKey());
					}
				});
				return ret;
			}

			private List<Commit> createRandomCommits(Key key) {
				Random random = new Random();
				List<Commit> ret = new ArrayList<Commit>();
				for (int count = 0; count < 5; count++) {
					Commit commit = new Commit();
					commit.setProject(key);
					long rev = Math.abs(random.nextLong());
					if (rev < HEX_10_DIGITS)
						rev += HEX_10_DIGITS + 1;
					commit.setRevision(Long.toString(rev, 16).substring(0, 8));
					commit.setDateTime(new Date(System.currentTimeMillis()
							- random.nextInt(3600000)));
					commit.setMessage("random commit with long message");
					commit.setAuthor("random");
					ret.add(commit);
				}
				return ret;
			}

			private List<Issue> createRandomIssues(Key key) {
				Random random = new Random();
				List<Issue> ret = new ArrayList<Issue>();
				for (int count = 0; count < 5; count++) {
					Issue issue = new Issue();
					issue.setProject(key);
					issue.setId(random.nextInt(100000));
					issue.setDateTime(new Date(System.currentTimeMillis()
							- random.nextInt(3600000)));
					issue.setSummary("random issue with long message");
					issue.setStatus(IssueStatus.NEW);
					issue.setSeverity(IssueSeverity.values()[random
							.nextInt(IssueSeverity.values().length)]);
					issue.setPriority(IssuePriority.values()[random
							.nextInt(IssuePriority.values().length)]);
					ret.add(issue);
				}
				return ret;
			}

			private synchronized List<Event> createRandomEvents(Key key) {

				List<Event> ret = eventCache.get(key);
				if (ret != null)
					return ret;

				Random random = new Random();
				ret = new ArrayList<Event>();
				for (int count = 0; count < 2; count++) {
					Event event = new Event();
					event.setKey(key);
					event.setTitle("random");
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DAY_OF_YEAR, random.nextInt(30));
					event.setDateTime(cal.getTime());
					event.setMajor(random.nextInt(5) == 0);
					if (event.isMajor())
						event.getTags().add("#major");
					event.setColor(Integer.toHexString(random
							.nextInt(256 * 256 * 256)));
					while (event.getColor().length() < 0)
						event.setColor("0" + event.getColor());
					event.setColor("#" + event.getColor());
					ret.add(event);
				}
				Collections.sort(ret, new Comparator<Event>() {
					@Override
					public int compare(Event o1, Event o2) {
						return o1.getDateTime().compareTo(o2.getDateTime());
					}
				});
				eventCache.put(key, ret);
				return ret;
			}

			private List<TwitterStatus> createRandomTweets(Key key) {
				Random random = new Random();
				List<TwitterStatus> ret = new ArrayList<TwitterStatus>();
				for (int count = 0; count < 10; count++) {
					TwitterStatus status = new TwitterStatus(key);
					status.setDate(new Date(System.currentTimeMillis()
							- random.nextInt(24 * 3600 * 1000)));
					status.setTags(Collections.<String> emptyList());
					status.setUser("random");
					status.setText("random tweet at " + count);
					ret.add(status);
				}
				return ret;
			}
		};
		return (T) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class[] { dataSource }, handler);
	}

	@Override
	public void run() {
		dataCache.clear();
	}
}
