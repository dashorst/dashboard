package nl.topicus.onderwijs.dashboard.modules;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.keys.Project;

public interface DashboardRepository {
	public <T extends DataSource<?>> void addDataSource(Key key,
			Class<T> datasourceType, T dataSource);

	public Collection<DataSource<?>> getData(Key key);

	public <T extends DataSource<?>> Map<Key, T> getData(Class<T> datasource);

	public List<Project> getProjects();

	public <T extends Key> List<T> getKeys(Class<T> keyType);
}
