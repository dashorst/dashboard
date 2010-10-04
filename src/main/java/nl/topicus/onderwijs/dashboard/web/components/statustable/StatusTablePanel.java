package nl.topicus.onderwijs.dashboard.web.components.statustable;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.odlabs.wiquery.core.commons.IWiQueryPlugin;
import org.odlabs.wiquery.core.commons.WiQueryResourceManager;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.commons.WiQueryUIPlugin;

@WiQueryUIPlugin
public class StatusTablePanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer projects;

	public StatusTablePanel(String id) {
		super(id);

		projects = new WebMarkupContainer("projects");
		add(projects);
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(StatusTablePanel.class,
				"jquery.ui.dashboardtablemaster.js");
	}

	@Override
	public JsStatement statement() {
		Options projectList = new Options();
		projectList.putLiteral("eduarte", "EduArte");
		projectList.putLiteral("atvo", "@VO");
		projectList.putLiteral("duo", "DUO");
		projectList.putLiteral("passepartout", "PassePartout");
		projectList.putLiteral("test", "Test");

		Options options = new Options();
		options.put("projects", projectList.getJavaScriptOptions().toString());
		JsQuery jsq = new JsQuery(projects);
		return jsq.$().chain("dashboardTableMaster",
				options.getJavaScriptOptions());
	}
}
