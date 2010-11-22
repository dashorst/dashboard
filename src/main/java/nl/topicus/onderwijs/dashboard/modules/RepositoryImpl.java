package nl.topicus.onderwijs.dashboard.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.topicus.onderwijs.dashboard.config.ISettings;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.keys.Project;
import nl.topicus.onderwijs.dashboard.keys.Summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("online")
public class RepositoryImpl implements DashboardRepository {
	@Autowired
	private ISettings settings;
	private Map<Key, Map<Class<? extends DataSource<?>>, DataSource<?>>> index1 = new HashMap<Key, Map<Class<? extends DataSource<?>>, DataSource<?>>>();
	private Map<Class<? extends DataSource<?>>, Map<Key, DataSource<?>>> index2 = new HashMap<Class<? extends DataSource<?>>, Map<Key, DataSource<?>>>();

	public RepositoryImpl() {
	}

	public <T extends DataSource<?>> void addDataSource(Key key,
			Class<T> datasourceType, T dataSource) {
		Map<Class<? extends DataSource<?>>, DataSource<?>> map = index1
				.get(key);
		if (map == null) {
			map = new HashMap<Class<? extends DataSource<?>>, DataSource<?>>();
			index1.put(key, map);
		}
		map.put(datasourceType, dataSource);

		Map<Key, DataSource<?>> map2 = index2.get(datasourceType);
		if (map2 == null) {
			map2 = new HashMap<Key, DataSource<?>>();
			index2.put(datasourceType, map2);
		}
		map2.put(key, dataSource);
	}

	public List<Project> getProjects() {
		return getKeys(Project.class);
	}

	public <T extends Key> List<T> getKeys(Class<T> keyType) {
		List<T> ret = new ArrayList<T>();
		for (Key curKey : settings.getKeys())
			if (keyType.isInstance(curKey))
				ret.add(keyType.cast(curKey));
		if (keyType.isAssignableFrom(Summary.class))
			ret.add(keyType.cast(Summary.get()));
		return ret;
	}

	public Collection<DataSource<?>> getData(Key key) {
		return index1.get(key).values();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataSource<?>> Map<Key, T> getData(Class<T> datasource) {
		Map<Key, T> ret = (Map<Key, T>) index2.get(datasource);
		if (ret == null)
			ret = Collections.emptyMap();
		return ret;
	}
}
