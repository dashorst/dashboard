package nl.topicus.onderwijs.dashboard.web.components.bargraph;

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
	private JsonResourceBehavior<BarData> dataResource;

	public BarGraphBarPanel(String id, IModel<String> model) {
		super(id, model);
		this.dataResource = new JsonResourceBehavior<BarData>(
				new AbstractReadOnlyModel<BarData>() {
					private static final long serialVersionUID = 1L;

					@Override
					public BarData getObject() {
						double value = Math.random() * 10.0;
						return new BarData(value, Long.toString(Math
								.round(value * 10)));
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
}
