package nl.topicus.onderwijs.dashboard.web.components.statustable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.topicus.onderwijs.dashboard.datasources.DataSourceAnnotationReader;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;
import nl.topicus.onderwijs.dashboard.modules.Key;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.components.JsonResourceBehavior;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.odlabs.wiquery.core.commons.IWiQueryPlugin;
import org.odlabs.wiquery.core.commons.WiQueryResourceManager;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.commons.WiQueryUIPlugin;
import org.odlabs.wiquery.ui.widget.WidgetJavascriptResourceReference;

@WiQueryUIPlugin
public class StatusTableColumnPanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;
	private JsonResourceBehavior<List<ColumnData>> dataResource;
	private IModel<String> scheme;
	private IModel<List<Class<? extends DataSource<?>>>> dataSources;

	public StatusTableColumnPanel(String id, IModel<String> scheme,
			IModel<List<Class<? extends DataSource<?>>>> dataSources) {
		super(id);
		this.scheme = scheme;
		this.dataSources = dataSources;

		add(new AttributeAppender("class", scheme, " "));
		this.dataResource = new JsonResourceBehavior<List<ColumnData>>(
				new AbstractReadOnlyModel<List<ColumnData>>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<ColumnData> getObject() {
						List<ColumnData> ret = new ArrayList<ColumnData>();
						retrieveDataFromApplication(ret);
						return ret;
					}
				});
		add(dataResource);
	}

	protected void retrieveDataFromApplication(List<ColumnData> ret) {
		for (Class<? extends DataSource<?>> curDataSource : dataSources
				.getObject()) {
			DataSourceSettings settings = DataSourceAnnotationReader
					.getSettings(curDataSource);
			ret.add(getColumn(settings.label(), curDataSource));
		}
	}

	private <T extends DataSource<?>> ColumnData getColumn(String label,
			Class<T> datasourceType) {
		Repository repository = WicketApplication.get().getRepository();
		ColumnData column = new ColumnData();
		column.setLabel(label);
		Map<Key, T> data = repository.getData(datasourceType);

		for (Entry<Key, T> entry : data.entrySet()) {
			Object value = entry.getValue().getValue();
			if (value == null)
				value = "n/a";
			if (!(value instanceof String || value instanceof Number || value instanceof List<?>))
				value = value.toString();

			column.getData().put(entry.getKey().getCode(), value);
		}
		return column;
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(StatusTableColumnPanel.class,
				"jquery.ui.dashboardstatustable.js");
	}

	@Override
	public JsStatement statement() {
		Options options = new Options();
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		List<String> conversions = new ArrayList<String>();
		List<String> htmlClasses = new ArrayList<String>();
		for (Class<? extends DataSource<?>> curDataSource : dataSources
				.getObject()) {
			DataSourceSettings settings = DataSourceAnnotationReader
					.getSettings(curDataSource);
			conversions.add(settings.conversion());
			htmlClasses.add(settings.htmlClass());
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			options.put("conversion", mapper.writeValueAsString(conversions));
			options.put("htmlClasses", mapper.writeValueAsString(htmlClasses));
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardStatusTable",
				options.getJavaScriptOptions());
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		scheme.detach();
		dataSources.detach();
	}
}
