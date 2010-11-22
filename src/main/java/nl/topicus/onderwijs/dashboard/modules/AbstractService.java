package nl.topicus.onderwijs.dashboard.modules;

import nl.topicus.onderwijs.dashboard.config.Settings;
import nl.topicus.onderwijs.dashboard.modules.topicus.Retriever;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractService implements Retriever {
	@Autowired
	private Settings settings;

	public AbstractService() {
	}

	public Settings getSettings() {
		return settings;
	}
}
