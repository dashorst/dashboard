package nl.topicus.onderwijs.dashboard.web.components.table;

import nl.topicus.onderwijs.dashboard.datasources.DataSourceAnnotationReader;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;
import nl.topicus.onderwijs.dashboard.modules.KeyProperty;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.components.JsonResourceBehavior;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.odlabs.wiquery.core.IWiQueryPlugin;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.commons.WiQueryUIPlugin;
import org.odlabs.wiquery.ui.widget.WidgetJavaScriptResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WiQueryUIPlugin
public class TablePanel extends Panel implements IWiQueryPlugin {
	private static final Logger log = LoggerFactory.getLogger(TablePanel.class);
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer table;
	private JsonResourceBehavior<Object> dataResource;
	private Class<? extends DataSource<?>> dataSource;
	private Key key;
	private boolean useKeyLabel;

	public TablePanel(String id, Class<? extends DataSource<?>> dataSource,
			Key key, boolean useKeyLabel) {
		super(id);
		this.dataSource = dataSource;
		this.key = key;
		this.useKeyLabel = useKeyLabel;

		this.dataResource = new JsonResourceBehavior<Object>(
				new AbstractReadOnlyModel<Object>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Object getObject() {
						try {
							DashboardRepository repository = WicketApplication
									.get().getRepository();
							return repository
									.getData(TablePanel.this.dataSource)
									.get(TablePanel.this.key).getValue();
						} catch (NullPointerException e) {
							log.error("Cannot find datasource for "
									+ TablePanel.this.dataSource.getName()
									+ " for " + TablePanel.this.key.getCode());
							return null;
						}
					}
				});
		add(dataResource);

		table = new WebMarkupContainer("table");
		add(table);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.renderJavaScriptReference(WidgetJavaScriptResourceReference
				.get());
		response.renderJavaScriptReference(new JavaScriptResourceReference(
				TablePanel.class, "jquery.ui.dashboardtable.js"));
		response.renderJavaScriptReference(new JavaScriptResourceReference(
				TablePanel.class, "dashboardnstable.js"));
		response.renderJavaScriptReference(new JavaScriptResourceReference(
				TablePanel.class, "dashboardalerttable.js"));
		response.renderJavaScriptReference(new JavaScriptResourceReference(
				TablePanel.class, "dashboardissuetable.js"));
		response.renderJavaScriptReference(new JavaScriptResourceReference(
				TablePanel.class, "dashboardcommittable.js"));
	}

	@Override
	public JsStatement statement() {
		DataSourceSettings settings = DataSourceAnnotationReader
				.getSettings(dataSource);
		KeyProperty keyProperty = DataSourceAnnotationReader
				.getKeyProperty(dataSource);
		Options options = new Options();
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		options.putLiteral("label",
				useKeyLabel ? key.getName() : settings.label());
		options.putLiteral("htmlClass", settings.htmlClass());
		options.putLiteral("conversion", settings.conversion());
		options.putLiteral("keyProperty", keyProperty.value());
		JsQuery jsq = new JsQuery(table);
		return jsq.$().chain("dashboardTable", options.getJavaScriptOptions());
	}
}
