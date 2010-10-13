package nl.topicus.onderwijs.dashboard.modules;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Repository {
	public <T extends DataSource<?>> void addDataSourceForProject(Project key,
			Class<T> datasourceType, T dataSource);

	public Collection<DataSource<?>> getData(Key key);

	public <T extends DataSource<?>> Map<Key, T> getData(Class<T> datasource);

	public List<Project> getProjects();

	public <T extends Key> List<T> getKeys(Class<T> keyType);
}
