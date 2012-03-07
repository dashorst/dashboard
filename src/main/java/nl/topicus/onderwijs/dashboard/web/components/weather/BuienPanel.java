package nl.topicus.onderwijs.dashboard.web.components.weather;

import java.util.Map;

import nl.topicus.onderwijs.dashboard.datasources.Buien;
import nl.topicus.onderwijs.dashboard.datatypes.BuienRadar;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.components.JsonResourceBehavior;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.odlabs.wiquery.core.IWiQueryPlugin;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.commons.WiQueryUIPlugin;
import org.odlabs.wiquery.ui.widget.WidgetJavaScriptResourceReference;

@WiQueryUIPlugin
public class BuienPanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;
	private JsonResourceBehavior<BuienRadar> buienRadar;

	public BuienPanel(String id, final Key key) {
		super(id);

		this.buienRadar = new JsonResourceBehavior<BuienRadar>(
				new AbstractReadOnlyModel<BuienRadar>() {
					private static final long serialVersionUID = 1L;

					@Override
					public BuienRadar getObject() {
						DashboardRepository repository = WicketApplication
								.get().getRepository();
						Map<Key, Buien> data = repository.getData(Buien.class);
						Buien buien = data.get(key);
						BuienRadar value = buien.getValue();
						return value;
					}
				});
		add(buienRadar);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.renderJavaScriptReference(WidgetJavaScriptResourceReference
				.get());
		response.renderJavaScriptReference(new JavaScriptResourceReference(
				BuienPanel.class, "jquery.ui.dashboardbuien.js"));
	}

	@Override
	public JsStatement statement() {
		Options options = new Options();
		options.putLiteral("dataUrl", buienRadar.getCallbackUrl().toString());
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardbuien", options.getJavaScriptOptions());
	}
}
