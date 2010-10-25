package nl.topicus.onderwijs.dashboard.modules;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import nl.topicus.onderwijs.dashboard.modules.hudson.HudsonService;
import nl.topicus.onderwijs.dashboard.persistence.config.ConfigurationRepository;

public class Keys {
	public static final Project PARNASSYS = new Project("parnassys",
			"ParnasSys");
	public static final Project ATVO = new Project("atvo", "@VO");
	public static final Project ATVO_OUDERS = new Project("atvo_ouders",
			"@VO Ouders");
	public static final Project PARNASSYS_OUDERS = new Project(
			"parnassys_ouders", "Parnassys Ouders");
	public static final Project IRIS = new Project("iris", "Iris+");
	public static final Project EDUARTE = new Project("eduarte", "EduArte");

	public static final Misc NS = new Misc("ns", "NS");
	public static final Misc SUMMARY = new Misc("summary", "Summary");

	public static void main(String[] args) {
		ConfigurationRepository repository = new ConfigurationRepository();

		generateDefaultConfiguration();

		// try to read the configuration;
		repository.getConfiguration(Settings.class);
	}

	public static void generateDefaultConfiguration() {
		try {
			ConfigurationRepository repository = new ConfigurationRepository();

			Settings settings = new Settings();

			settings.addKey(ATVO);
			settings.addKey(ATVO_OUDERS);
			settings.addKey(EDUARTE);
			settings.addKey(PARNASSYS_OUDERS);
			settings.addKey(PARNASSYS);
			settings.addKey(IRIS);
			settings.addKey(NS);
			settings.addKey(SUMMARY);

			configureHudson(settings);

			repository.putConfiguration(settings);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void configureHudson(Settings settings) {
		HashMap<Project, List<String>> configuration = new HashMap<Project, List<String>>();

		configuration.put(Keys.ATVO, Arrays.asList("Vocus \\- (.*)"));
		configuration.put(Keys.ATVO_OUDERS, Arrays.asList("Vocus Ouders"));
		configuration.put(Keys.EDUARTE, Arrays.asList("EduArte v(.*)"));
		configuration.put(Keys.IRIS, Arrays.asList("Cluedo"));

		for (Entry<Project, List<String>> value : configuration.entrySet()) {
			HashMap<String, Object> entry = new HashMap<String, Object>();
			entry.put("matchers", value.getValue());
			entry.put("url", "http://192.168.55.113");
			settings.addProjectSettings(value.getKey(), HudsonService.class
					.getName(), entry);
		}
	}
}
