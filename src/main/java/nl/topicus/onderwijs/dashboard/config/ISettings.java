package nl.topicus.onderwijs.dashboard.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.topicus.onderwijs.dashboard.keys.Key;

public interface ISettings {

	public List<Key> getKeys();

	public Set<Key> getKeysWithConfigurationFor(Class<?> service);

	public Map<Key, Map<String, ?>> getServiceSettings(Class<?> service);
}
