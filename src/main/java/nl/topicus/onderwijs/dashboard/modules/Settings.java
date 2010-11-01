package nl.topicus.onderwijs.dashboard.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

public class Settings {
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

	private static ArrayList<Key> keys = new ArrayList<Key>();

	private HashMap<Project, Map<String, Map<String, ?>>> projectSettings = new HashMap<Project, Map<String, Map<String, ?>>>();

	@JsonIgnore
	public List<Project> getProjects() {
		ArrayList<Project> projects = new ArrayList<Project>();
		for (Key key : projects) {
			if (key instanceof Project)
				projects.add(Project.class.cast(key));
		}
		return projects;
	}

	public void addKey(Key key) {
		if (!keys.contains(key))
			keys.add(key);
	}

	public List<Key> getKeys() {
		return Collections.unmodifiableList(keys);
	}

	public void setKeys(List<Key> projects) {
		Settings.keys = new ArrayList<Key>(projects);
	}

	public Map<Project, Map<String, Map<String, ?>>> getProjectSettings() {
		return projectSettings;
	}

	@JsonDeserialize(keyUsing = ProjectKeyDeserializer.class)
	public void setProjectSettings(
			Map<Project, Map<String, Map<String, ?>>> projectSettings) {
		this.projectSettings = new HashMap<Project, Map<String, Map<String, ?>>>(
				projectSettings);
	}

	public void addProjectSettings(Project project, String setter,
			Map<String, ?> settings) {
		Map<String, Map<String, ?>> map = projectSettings.get(project);
		if (map == null) {
			map = new HashMap<String, Map<String, ?>>();
			projectSettings.put(project, map);
		}
		map.put(setter, settings);
	}

	@SuppressWarnings( { "unchecked", "rawtypes" })
	public Map<String, ?> getProjectSettings(Project project, Class<?> service) {
		Map<String, Map<String, ?>> map = projectSettings.get(project);
		if (map != null) {
			return map.get(service.getName());
		}
		return new HashMap();
	}

	public Map<Key, Map<String, ?>> getServiceSettings(Class<?> service) {
		Map<Key, Map<String, ?>> serviceSettings = new HashMap<Key, Map<String, ?>>();
		String key = service.getName();

		for (Entry<Project, Map<String, Map<String, ?>>> projectSetting : projectSettings
				.entrySet()) {
			Map<String, Map<String, ?>> settings = projectSetting.getValue();
			if (settings.containsKey(key)) {
				serviceSettings.put(projectSetting.getKey(), settings.get(key));
			}
		}
		return serviceSettings;
	}
}
