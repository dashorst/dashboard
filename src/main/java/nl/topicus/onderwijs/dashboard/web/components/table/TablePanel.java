package nl.topicus.onderwijs.dashboard.web.components.table;

import nl.topicus.onderwijs.dashboard.datasources.DataSourceAnnotationReader;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;
import nl.topicus.onderwijs.dashboard.modules.Key;
import nl.topicus.onderwijs.dashboard.modules.KeyProperty;
import nl.topicus.onderwijs.dashboard.modules.Repository;
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
	private JsonResourceBehavior<Object> dataResource;
	private Class<? extends DataSource<?>> dataSource;
	private Key key;

	public TablePanel(String id, Class<? extends DataSource<?>> dataSource,
			Key key) {
		super(id);
		this.dataSource = dataSource;
		this.key = key;

		this.dataResource = new JsonResourceBehavior<Object>(
				new AbstractReadOnlyModel<Object>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Object getObject() {
						Repository repository = WicketApplication.get()
								.getRepository();
						return repository.getData(TablePanel.this.dataSource)
								.get(TablePanel.this.key).getValue();
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
		manager.addJavaScriptResource(TablePanel.class,
				"dashboardalerttable.js");
	}

	@Override
	public JsStatement statement() {
		DataSourceSettings settings = DataSourceAnnotationReader
				.getSettings(dataSource);
		KeyProperty keyProperty = DataSourceAnnotationReader
				.getKeyProperty(dataSource);
		Options options = new Options();
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		options.putLiteral("label", settings.label());
		options.putLiteral("htmlClass", settings.htmlClass());
		options.putLiteral("conversion", settings.conversion());
		options.putLiteral("keyProperty", keyProperty.value());
		JsQuery jsq = new JsQuery(table);
		return jsq.$().chain("dashboardTable", options.getJavaScriptOptions());
	}
}
