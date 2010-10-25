package nl.topicus.onderwijs.dashboard.modules;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import nl.topicus.onderwijs.dashboard.datasources.Alerts;
import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.datatypes.DotColor;
import nl.topicus.onderwijs.dashboard.modules.ns.model.Train;
import nl.topicus.onderwijs.dashboard.modules.ns.model.TrainType;

import org.apache.wicket.util.time.Duration;

public class RandomDataRepositoryImpl extends TimerTask implements Repository {
	private Repository base;
	private Set<Class<? extends DataSource<?>>> sources = new HashSet<Class<? extends DataSource<?>>>();
	private ConcurrentHashMap<String, Object> dataCache = new ConcurrentHashMap<String, Object>();
	private Timer timer;

	public RandomDataRepositoryImpl(Repository base) {
		this.base = base;
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
		if (key.equals(Keys.SUMMARY))
			return base.getData(dataSource).get(key);
		// summary is a special case, use the original datasource
		if (dataSource.equals(Alerts.class) && !(key instanceof Project))
			return null;

		final DataSourceSettings settings = dataSource
				.getAnnotation(DataSourceSettings.class);
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				if (method.getName().equals("getValue")) {
					String dataKey = key.getCode() + "-" + dataSource.getName();
					Object value;
					if (settings.type().equals(Integer.class))
						value = Math.round(Math.random() * 1000);
					else if (settings.type().equals(Duration.class))
						value = Duration.milliseconds(Math
								.round(Math.random() * 100000000));
					else if (settings.type().equals(String.class))
						value = "random";
					else if (settings.type().equals(DotColor.class)
							&& settings.list()) {
						Random random = new Random();
						List<DotColor> ret = new ArrayList<DotColor>();
						for (int count = 4; count >= 0; count--) {
							ret.add(DotColor.values()[random.nextInt(4)]);
						}
						value = ret;
					} else if (settings.type().equals(Train.class)
							&& settings.list()) {
						value = createRandomTrains();
					} else if (settings.type().equals(Alert.class)
							&& settings.list()) {
						value = createRandomAlerts(key);
					} else
						throw new IllegalStateException("Unsupported type "
								+ settings.type());
					Object ret = dataCache.putIfAbsent(dataKey, value);
					return ret == null ? value : ret;
				} else if (method.getName().equals("toString")) {
					String dataKey = key.getCode() + "-" + dataSource.getName();
					return dataKey;
				}
				throw new UnsupportedOperationException();
			}

			private List<Train> createRandomTrains() {
				Random random = new Random();
				List<Train> ret = new ArrayList<Train>();
				for (int count = 0; count < 10; count++) {
					Train train = new Train();
					train.setType(TrainType.values()[random.nextInt(TrainType
							.values().length)]);
					train.setDestination("random");
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
					alert.setProject((Project) key);
					alert.setColor(DotColor.values()[random.nextInt(3)]);
					int minute = random.nextInt(60);
					alert.setTime(random.nextInt(24) + ":"
							+ (minute < 10 ? "0" : "") + minute);
					alert.setMessage("random");
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
		};
		return (T) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class[] { dataSource }, handler);
	}

	@Override
	public void run() {
		dataCache.clear();
	}
}
