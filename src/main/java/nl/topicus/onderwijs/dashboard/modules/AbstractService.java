package nl.topicus.onderwijs.dashboard.modules;

import nl.topicus.onderwijs.dashboard.config.ISettings;
import nl.topicus.onderwijs.dashboard.modules.topicus.Retriever;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractService implements Retriever {
	@Autowired
	private ISettings settings;

	public AbstractService() {
	}

	public ISettings getSettings() {
		return settings;
	}
}
