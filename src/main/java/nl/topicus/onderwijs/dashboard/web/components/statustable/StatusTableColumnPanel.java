package nl.topicus.onderwijs.dashboard.web.components.statustable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
						for (int count = 0; count < 4; count++) {
							ColumnData curData = new ColumnData();
							curData.setLabel("Label-" + count);
							for (String curProject : Arrays.asList("eduarte",
									"atvo", "duo", "passepartout", "test")) {
								curData.getData().put(curProject,
										Math.round(Math.random() * 1000));
							}
							ret.add(curData);
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
		manager.addJavaScriptResource(StatusTableColumnPanel.class,
				"jquery.ui.dashboardtable.js");
	}

	@Override
	public JsStatement statement() {
		Options options = new Options();
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardTable", options.getJavaScriptOptions());
	}
}
