package nl.topicus.onderwijs.dashboard.web.components.statustable;

import java.util.Arrays;

import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.odlabs.wiquery.core.commons.IWiQueryPlugin;
import org.odlabs.wiquery.core.commons.WiQueryResourceManager;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.commons.WiQueryUIPlugin;
import org.odlabs.wiquery.ui.widget.WidgetJavascriptResourceReference;

@WiQueryUIPlugin
public class StatusTablePanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer projects;

	public StatusTablePanel(String id) {
		super(id);

		projects = new WebMarkupContainer("projects");
		add(projects);

		ListView<String> columns = new ListView<String>("columns", Arrays
				.asList("color-1", "color-2", "color-3", "color-4")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<String> item) {
				item.add(new StatusTableColumnPanel("column", item.getModel()));
			}
		};
		add(columns);
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(StatusTablePanel.class,
				"jquery.ui.dashboardtablemaster.js");
	}

	@Override
	public JsStatement statement() {
		Options projectList = new Options();
		for (Project project : WicketApplication.get().getProjects()) {
			projectList.putLiteral(project.getCode(), project.getName());
		}

		Options options = new Options();
		options.put("projects", projectList.getJavaScriptOptions().toString());
		JsQuery jsq = new JsQuery(projects);
		return jsq.$().chain("dashboardTableMaster",
				options.getJavaScriptOptions());
	}
}
