package nl.topicus.onderwijs.dashboard.web.components.bargraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.topicus.onderwijs.dashboard.web.components.JsonResourceBehavior;
import nl.topicus.onderwijs.dashboard.web.components.statustable.StatusTablePanel;

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
	private IModel<List<String>> dataSetsModel;

	public BarGraphBarPanel(String id, IModel<String> projectNameModel,
			IModel<List<String>> dataSetsModel) {
		super(id, projectNameModel);
		this.dataSetsModel = dataSetsModel;
		this.dataResource = new JsonResourceBehavior<Map<String, BarData>>(
				new AbstractReadOnlyModel<Map<String, BarData>>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Map<String, BarData> getObject() {
						Map<String, BarData> ret = new HashMap<String, BarData>();
						int index = 0;
						for (String curDataSet : BarGraphBarPanel.this.dataSetsModel
								.getObject()) {
							index++;
							double value = Math.random() * 10.0;
							ret.put(curDataSet, new BarData(value, Long
									.toString(Math.round(value * 10) * index)));
						}
						return ret;
					}
				});
		add(dataResource);
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(StatusTablePanel.class,
				"jquery.timers-1.1.3.js");
		manager.addJavaScriptResource(BarGraphBarPanel.class,
				"jquery.ui.dashboardbargraph.js");
	}

	@Override
	public JsStatement statement() {
		Options options = new Options();
		options.putLiteral("projectName", getDefaultModelObjectAsString());
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
