package nl.topicus.onderwijs.dashboard.web.components.twitter;

import nl.topicus.onderwijs.dashboard.datasources.TwitterMentions;
import nl.topicus.onderwijs.dashboard.datasources.TwitterTimeline;
import nl.topicus.onderwijs.dashboard.keys.Key;
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
public class TwitterPanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;
	private JsonResourceBehavior<TwitterData> dataResource;

	public TwitterPanel(String id, final Key key) {
		super(id);

		this.dataResource = new JsonResourceBehavior<TwitterData>(
				new AbstractReadOnlyModel<TwitterData>() {
					private static final long serialVersionUID = 1L;

					@Override
					public TwitterData getObject() {
						Repository repository = WicketApplication.get()
								.getRepository();
						return new TwitterData(repository.getData(
								TwitterTimeline.class).get(key).getValue(),
								repository.getData(TwitterMentions.class).get(
										key).getValue());
					}
				});
		add(dataResource);
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(TwitterPanel.class,
				"jquery.ui.dashboardtwitter.js");
	}

	@Override
	public JsStatement statement() {
		Options options = new Options();
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		JsQuery jsq = new JsQuery(this);
		return jsq.$()
				.chain("dashboardTwitter", options.getJavaScriptOptions());
	}
}
