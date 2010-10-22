package nl.topicus.onderwijs.dashboard.web.components.table;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Trains;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;
import nl.topicus.onderwijs.dashboard.modules.Keys;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.modules.ns.model.Train;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.components.JsonResourceBehavior;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.odlabs.wiquery.core.commons.IWiQueryPlugin;
import org.odlabs.wiquery.core.commons.WiQueryResourceManager;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.commons.WiQueryUIPlugin;
import org.odlabs.wiquery.ui.widget.WidgetJavascriptResourceReference;

@WiQueryUIPlugin
public class TablePanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer table;
	private JsonResourceBehavior<List<Train>> dataResource;

	public TablePanel(String id) {
		super(id);

		this.dataResource = new JsonResourceBehavior<List<Train>>(
				new AbstractReadOnlyModel<List<Train>>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<Train> getObject() {
						Repository repository = WicketApplication.get()
								.getRepository();
						return repository.getData(Trains.class).get(Keys.NS)
								.getValue();
					}
				});
		add(dataResource);

		table = new WebMarkupContainer("table");
		add(table);
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(TablePanel.class,
				"jquery.ui.dashboardtable.js");
		manager.addJavaScriptResource(TablePanel.class, "dashboardnstable.js");
	}

	@Override
	public JsStatement statement() {
		DataSourceSettings settings = Trains.class
				.getAnnotation(DataSourceSettings.class);
		Options options = new Options();
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		options.putLiteral("label", settings.label());
		options.putLiteral("htmlClass", settings.htmlClass());
		options.putLiteral("conversion", settings.conversion());
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardTable", options.getJavaScriptOptions());
	}
}
