package nl.topicus.onderwijs.dashboard.web.components.bargraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.Key;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.components.JsonResourceBehavior;

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
public class BarGraphBarPanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;
	private JsonResourceBehavior<Map<String, BarData>> dataResource;
	private IModel<List<Class<? extends DataSource<? extends Number>>>> dataSetsModel;

	public BarGraphBarPanel(
			String id,
			IModel<Project> projectModel,
			IModel<List<Class<? extends DataSource<? extends Number>>>> dataSetsModel) {
		super(id, projectModel);
		this.dataSetsModel = dataSetsModel;
		this.dataResource = new JsonResourceBehavior<Map<String, BarData>>(
				new AbstractReadOnlyModel<Map<String, BarData>>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Map<String, BarData> getObject() {
						return retrieveDataFromApplication();
					}
				});
		add(dataResource);
	}

	protected Map<String, BarData> retrieveDataFromApplication() {
		Map<String, BarData> ret = new HashMap<String, BarData>();
		for (Class<? extends DataSource<? extends Number>> datasource : dataSetsModel
				.getObject()) {
			getDataFromDataSource(ret, datasource);
		}
		return ret;
	}

	private void getDataFromDataSource(Map<String, BarData> ret,
			Class<? extends DataSource<? extends Number>> datasourceType) {
		Repository repository = WicketApplication.get().getRepository();

		Project project = getProject();

		Map<Key, ? extends DataSource<? extends Number>> data = repository
				.getData(datasourceType);
		DataSource<? extends Number> datasource = data.get(project);

		if (datasource != null) {
			long max = 0;
			for (DataSource<? extends Number> status : data.values()) {
				Number number = status.getValue();
				max = Math.max(number == null ? 0 : number.longValue(), max);
			}

			Number sourceValue = datasource.getValue();
			sourceValue = sourceValue == null ? Long.valueOf(0) : sourceValue;
			long number = sourceValue == null ? 0 : sourceValue.longValue();
			double value = (10.0 * number) / max;
			ret.put(datasourceType.getSimpleName(), new BarData(value,
					sourceValue.toString()));
		} else {
			ret.put(datasourceType.getSimpleName(), new BarData(0, "n/a"));
		}
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(BarGraphBarPanel.class,
				"jquery.ui.dashboardbargraph.js");
	}

	public Project getProject() {
		return (Project) getDefaultModelObject();
	}

	@Override
	public JsStatement statement() {
		Options options = new Options();
		options.putLiteral("projectName", getProject().getName());
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardBarGraph",
				options.getJavaScriptOptions());
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		dataSetsModel.detach();
	}
}
