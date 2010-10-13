package nl.topicus.onderwijs.dashboard.modules;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wicket.util.time.Duration;

public class RandomDataRepositoryImpl extends TimerTask implements Repository {
	private Repository base;
	private Set<Class<? extends DataSource<?>>> sources = new HashSet<Class<? extends DataSource<?>>>();
	private ConcurrentHashMap<String, Object> dataCache = new ConcurrentHashMap<String, Object>();

	public RandomDataRepositoryImpl(Repository base) {
		this.base = base;
		Timer timer = new Timer("Random Data Updater", true);
		timer.scheduleAtFixedRate(this, 0, Duration.seconds(5)
				.getMilliseconds());
	}

	public <T extends DataSource<?>> void addDataSourceForProject(Project key,
			Class<T> datasourceType, T dataSource) {
		sources.add(datasourceType);
		base.addDataSourceForProject(key, datasourceType, dataSource);
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
			ret.put(curKey, createDataSource(curKey, datasource));
		}
		return ret;
	}

	private <T extends DataSource<?>> T createDataSource(final Key key,
			final Class<T> dataSource) {
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
					else
						throw new IllegalStateException("Unsupported type "
								+ settings.type());
					Object ret = dataCache.putIfAbsent(dataKey, value);
					return ret == null ? value : ret;
				}
				throw new UnsupportedOperationException();
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
