package nl.topicus.onderwijs.dashboard.api;

import org.apache.wicket.markup.html.panel.Panel;

public interface WebConfigurableModule {
	public Panel getWebConfigurationEditor(String id);
}
