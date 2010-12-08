package nl.topicus.onderwijs.dashboard.web.components.table;

import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.odlabs.wiquery.core.commons.IWiQueryPlugin;
import org.odlabs.wiquery.core.commons.WiQueryResourceManager;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.commons.WiQueryUIPlugin;
import org.odlabs.wiquery.ui.widget.WidgetJavascriptResourceReference;

@WiQueryUIPlugin
public class StackedTablesPanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;
	private RepeatingView tables;

	public StackedTablesPanel(String id) {
		super(id);

		tables = new RepeatingView("tables");
		add(tables);
	}

	public String nextTableId() {
		return tables.newChildId();
	}

	public void addTable(Panel table) {
		tables.add(table);
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(StackedTablesPanel.class,
				"jquery.ui.dashboardstackedtables.js");
	}

	@Override
	public JsStatement statement() {
		Options options = new Options();
		options.put("secondsBetweenSwitch", WicketApplication.get()
				.isDevelopment() ? 30 : 60);
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardStackedTables",
				options.getJavaScriptOptions());
	}
}
