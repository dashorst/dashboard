package nl.topicus.onderwijs.dashboard.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.keys.Project;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

public class Settings implements ISettings {

	/**
	 * Custom JSON deserializer for projects that are used as key values in a
	 * Map.
	 */
	private static class ProjectKeyDeserializer extends KeyDeserializer {
		@Override
		public Object deserializeKey(String id, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			for (Key key : keys) {
				if (key.getCode().equals(id))
					return key;
			}
			throw ctxt.weirdKeyException(String.class, id,
					"Onbekend project sleutel gevonden");
		}
	}

	public static ISettings create() {
		ConfigurationRepository configurationRepository = new ConfigurationRepository();
		if (!configurationRepository.configurationExists(Settings.class))
			throw new IllegalStateException("No configuration exists for "
					+ Settings.class.getName());
		return configurationRepository.getConfiguration(Settings.class);
	}

	private static ArrayList<Key> keys = new ArrayList<Key>();

	private HashMap<Key, Map<String, Map<String, ?>>> projectSettings = new HashMap<Key, Map<String, Map<String, ?>>>();

	public void addKey(Key key) {
		if (!keys.contains(key))
			keys.add(key);
	}

	@Override
	public List<Key> getKeys() {
		return Collections.unmodifiableList(keys);
	}

	public void setKeys(List<Key> projects) {
		Settings.keys = new ArrayList<Key>(projects);
	}

	public Map<Key, Map<String, Map<String, ?>>> getProjectSettings() {
		return projectSettings;
	}

	@JsonDeserialize(keyUsing = ProjectKeyDeserializer.class)
	public void setProjectSettings(
			Map<Project, Map<String, Map<String, ?>>> projectSettings) {
		this.projectSettings = new HashMap<Key, Map<String, Map<String, ?>>>(
				projectSettings);
	}

	@Override
	public Map<Key, Map<String, ?>> getServiceSettings(Class<?> service) {
		Map<Key, Map<String, ?>> serviceSettings = new HashMap<Key, Map<String, ?>>();
		String key = service.getName();

		for (Entry<Key, Map<String, Map<String, ?>>> projectSetting : projectSettings
				.entrySet()) {
			Map<String, Map<String, ?>> settings = projectSetting.getValue();
			if (settings.containsKey(key)) {
				serviceSettings.put(projectSetting.getKey(), settings.get(key));
			}
		}
		return serviceSettings;
	}

	@Override
	public Set<Key> getKeysWithConfigurationFor(Class<?> service) {
		return getServiceSettings(service).keySet();
	}
}
