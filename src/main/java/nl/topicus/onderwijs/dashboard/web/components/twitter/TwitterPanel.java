package nl.topicus.onderwijs.dashboard.web.components.twitter;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.TwitterMentions;
import nl.topicus.onderwijs.dashboard.datasources.TwitterTimeline;
import nl.topicus.onderwijs.dashboard.datatypes.TwitterStatus;
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
	private JsonResourceBehavior<List<TwitterStatus>> timelineDataResource;
	private JsonResourceBehavior<List<TwitterStatus>> mentionsDataResource;

	public TwitterPanel(String id, final Key key) {
		super(id);

		this.timelineDataResource = new JsonResourceBehavior<List<TwitterStatus>>(
				new AbstractReadOnlyModel<List<TwitterStatus>>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<TwitterStatus> getObject() {
						Repository repository = WicketApplication.get()
								.getRepository();
						return repository.getData(TwitterTimeline.class).get(
								key).getValue();
					}
				});
		add(timelineDataResource);

		this.mentionsDataResource = new JsonResourceBehavior<List<TwitterStatus>>(
				new AbstractReadOnlyModel<List<TwitterStatus>>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<TwitterStatus> getObject() {
						Repository repository = WicketApplication.get()
								.getRepository();
						return repository.getData(TwitterMentions.class).get(
								key).getValue();
					}
				});
		add(mentionsDataResource);
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
		options.putLiteral("timelineUrl", timelineDataResource.getCallbackUrl()
				.toString());
		options.putLiteral("mentionsUrl", mentionsDataResource.getCallbackUrl()
				.toString());
		JsQuery jsq = new JsQuery(this);
		return jsq.$()
				.chain("dashboardTwitter", options.getJavaScriptOptions());
	}
}
