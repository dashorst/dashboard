package nl.topicus.onderwijs.dashboard.web.components.events;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datatypes.Event;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.Key;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.components.JsonResourceBehavior;

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
public class EventsPanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;
	private JsonResourceBehavior<EventData> dataResource;
	private Class<? extends DataSource<List<Event>>> dataSource;
	private Key key;

	public EventsPanel(String id,
			Class<? extends DataSource<List<Event>>> dataSource, Key key) {
		super(id);
		this.dataSource = dataSource;
		this.key = key;

		this.dataResource = new JsonResourceBehavior<EventData>(
				new AbstractReadOnlyModel<EventData>() {
					private static final long serialVersionUID = 1L;

					@Override
					public EventData getObject() {
						Repository repository = WicketApplication.get()
								.getRepository();
						return new EventData(repository.getData(
								EventsPanel.this.dataSource).get(
								EventsPanel.this.key).getValue());
					}
				});
		add(dataResource);
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(EventsPanel.class,
				"jquery.ui.dashboardevents.js");
	}

	@Override
	public JsStatement statement() {
		Options options = new Options();
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardEvents", options.getJavaScriptOptions());
	}
}
