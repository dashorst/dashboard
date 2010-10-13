package nl.topicus.onderwijs.dashboard.web.components.statustable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.topicus.onderwijs.dashboard.datasources.ApplicationVersion;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfServers;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfServersOffline;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.datasources.Uptime;
import nl.topicus.onderwijs.dashboard.datatypes.DotColor;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.Key;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.web.DashboardMode;
import nl.topicus.onderwijs.dashboard.web.DashboardWebSession;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.components.JsonResourceBehavior;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
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

	public StatusTableColumnPanel(String id, IModel<String> scheme) {
		super(id, scheme);
		add(new AttributeAppender("class", scheme, " "));
		this.dataResource = new JsonResourceBehavior<List<ColumnData>>(
				new AbstractReadOnlyModel<List<ColumnData>>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<ColumnData> getObject() {
						List<ColumnData> ret = new ArrayList<ColumnData>();
						if (DashboardWebSession.get().getMode() == DashboardMode.RandomData) {
							generateRandomData(ret);
						} else {
							retrieveDataFromApplication(ret);
						}
						return ret;
					}
				});
		add(dataResource);
	}

	protected void retrieveDataFromApplication(List<ColumnData> ret) {
		if ("color-1".equals(getDefaultModelObjectAsString())) {
			ret.add(getColumn("Current users", NumberOfUsers.class));
			ret.add(getColumn("Current users", NumberOfUsers.class));
		}
		if ("color-2".equals(getDefaultModelObjectAsString())) {
			ret.add(getColumn("Version", ApplicationVersion.class));
			ret.add(getColumn("Version", ApplicationVersion.class));
		}
		if ("color-3".equals(getDefaultModelObjectAsString())) {
			ret.add(getColumn("Uptime", Uptime.class));
			ret.add(getColumn("Uptime", Uptime.class));
		}
		if ("color-4".equals(getDefaultModelObjectAsString())) {
			ret.add(getColumn("#Servers", NumberOfServers.class));
			ret
					.add(getColumn("#Offline servers",
							NumberOfServersOffline.class));
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
			if (!(value instanceof String || value instanceof Number))
				value = value.toString();

			column.getData().put(entry.getKey().getCode(), value);
		}
		return column;
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(StatusTableColumnPanel.class,
				"jquery.ui.dashboardtable.js");
	}

	@Override
	public JsStatement statement() {
		Options options = new Options();
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		if ("color-4".equals(getDefaultModelObjectAsString()))
			options.putLiteral("conversion", "dots");
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardTable", options.getJavaScriptOptions());
	}

	private void generateRandomData(List<ColumnData> ret) {
		for (int count = 0; count < 4; count++) {
			ColumnData curData = new ColumnData();
			curData.setLabel("Label-" + count);
			for (Project curProject : WicketApplication.get().getProjects()) {
				if ("color-4".equals(getDefaultModelObjectAsString())) {
					List<DotColor> colors = new ArrayList<DotColor>();
					for (int dotCount = 0; dotCount < 5; dotCount++)
						colors.add(DotColor.values()[(int) Math.floor(Math
								.random() * 4)]);
					curData.getData().put(curProject.getCode(), colors);
				} else {
					curData.getData().put(curProject.getCode(),
							Math.round(Math.random() * 1000));
				}
			}
			ret.add(curData);
		}
	}
}
