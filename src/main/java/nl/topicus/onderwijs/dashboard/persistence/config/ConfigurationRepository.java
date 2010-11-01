package nl.topicus.onderwijs.dashboard.persistence.config;

import java.io.File;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class ConfigurationRepository {
	/**
	 * Returns whether the configuration file for clz exists.
	 */
	public boolean configurationExists(Class<?> clz) {
		File file = getConfigFile(clz);
		return file.exists();
	}

	/**
	 * Gets the configuration from the home directory of the user running the
	 * dashboard. The configuration is stored in a JSON format, using the
	 * configuration fully qualified classname as the filename.
	 */
	public <T> T getConfiguration(Class<T> clz) {
		File configFilename = getConfigFile(clz);

		T config = newConfig(clz);
		if (!configFilename.exists()) {
			return config;
		}

		ObjectMapper mapper = getJsonMapper();
		try {
			config = mapper.readValue(configFilename, clz);
		} catch (Exception e) {
			System.err.println("Unable to read configuration " + configFilename
					+ ": " + e.getMessage());
			e.printStackTrace();
		}
		return config;
	}

	private ObjectMapper getJsonMapper() {
		ObjectMapper mapper = new ObjectMapper();
		return mapper;
	}

	private <T> File getConfigFile(Class<T> clz) {
		File homedir = new File(System.getProperty("user.home"));
		File configdir = new File(homedir, ".dashboard");
		if (!configdir.exists()) {
			configdir.mkdir();
			configdir.setReadable(true, true);
			configdir.setExecutable(true, true);
			configdir.setWritable(true, true);
		}
		if (!configdir.exists()) {
			System.err.println("Unable to create " + configdir
					+ " to store dashboard configuration. Exiting...");
			System.exit(1);
		}

		File configFilename = new File(configdir, clz.getName() + ".json");
		return configFilename;
	}

	private <T> T newConfig(Class<T> clz) {
		try {
			return clz.newInstance();
		} catch (Exception e) {
			System.err.println("Unable to instantiate " + clz
					+ " for a new configuration, exiting...");
			System.exit(1);
			return null;
		}
	}

	/**
	 * Writes the configuration to the home directory of the user running the
	 * dashboard. The configuration is stored in a JSON format, using the
	 * configuration fully qualified classname as the filename.
	 */
	public <T> void putConfiguration(T config) {
		ObjectMapper mapper = getJsonMapper();
		File configFilename = getConfigFile(config.getClass());
		try {
			mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
			mapper.writeValue(configFilename, config);
		} catch (Exception e) {
			System.err.println("Unable to write configuration to "
					+ configFilename + ": " + e.getMessage());
		}
	}
}
