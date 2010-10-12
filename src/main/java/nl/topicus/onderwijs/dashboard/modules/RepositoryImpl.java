package nl.topicus.onderwijs.dashboard.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositoryImpl implements Repository {
	private List<Key> keys = Arrays.asList((Key) new Project("parnassys",
			"ParnasSys"), new Project("parnassys_ouders",
			"ParnasSys Ouderportaal"), new Project("atvo", "@VO"), new Project(
			"atvo_ouders", "@VO Ouderportaal"),
			new Project("irisplus", "Iris+"));

	private Map<Key, Map<Class<? extends DataSource<?>>, DataSource<?>>> index1 = new HashMap<Key, Map<Class<? extends DataSource<?>>, DataSource<?>>>();
	private Map<Class<? extends DataSource<?>>, Map<Key, DataSource<?>>> index2 = new HashMap<Class<? extends DataSource<?>>, Map<Key, DataSource<?>>>();

	public RepositoryImpl() {
	}

	public <T extends DataSource<?>> void addDataSourceForProject(Project key,
			Class<T> datasourceType, T dataSource) {
		HashMap<Class<? extends DataSource<?>>, DataSource<?>> map = new HashMap<Class<? extends DataSource<?>>, DataSource<?>>();
		map.put(datasourceType, dataSource);
		index1.put(key, map);

		HashMap<Key, DataSource<?>> map2 = new HashMap<Key, DataSource<?>>();
		map2.put(key, dataSource);
		index2.put(datasourceType, map2);
	}

	public List<Project> getProjects() {
		return getKeys(Project.class);
	}

	public <T extends Key> List<T> getKeys(Class<T> keyType) {
		List<T> ret = new ArrayList<T>();
		for (Key curKey : keys)
			if (keyType.isInstance(curKey))
				ret.add(keyType.cast(curKey));
		return ret;
	}

	public Collection<DataSource<?>> getData(Key key) {
		return index1.get(key).values();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataSource<?>> Map<Key, T> getData(Class<T> datasource) {
		return (Map<Key, T>) index2.get(datasource);
	}
}
