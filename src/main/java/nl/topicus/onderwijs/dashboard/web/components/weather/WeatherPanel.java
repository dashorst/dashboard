package nl.topicus.onderwijs.dashboard.web.components.weather;

import nl.topicus.onderwijs.dashboard.datasources.Weather;
import nl.topicus.onderwijs.dashboard.datatypes.WeatherReport;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
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
public class WeatherPanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;
	private JsonResourceBehavior<WeatherReport> dataResource;
	private WebMarkupContainer panel;

	public WeatherPanel(String id, final Key key) {
		super(id);

		this.dataResource = new JsonResourceBehavior<WeatherReport>(
				new AbstractReadOnlyModel<WeatherReport>() {
					private static final long serialVersionUID = 1L;

					@Override
					public WeatherReport getObject() {
						DashboardRepository repository = WicketApplication.get()
								.getRepository();
						return repository.getData(Weather.class).get(key)
								.getValue();
					}
				});
		add(dataResource);
		add(panel = new WebMarkupContainer("panel"));
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(WeatherPanel.class,
				"jquery.ui.dashboardweather.js");
	}

	@Override
	public JsStatement statement() {
		Options options = new Options();
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		JsQuery jsq = new JsQuery(panel);
		return jsq.$()
				.chain("dashboardWeather", options.getJavaScriptOptions());
	}
}
