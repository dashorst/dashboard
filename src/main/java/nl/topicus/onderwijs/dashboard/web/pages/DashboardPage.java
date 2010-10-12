package nl.topicus.onderwijs.dashboard.web.pages;

import nl.topicus.onderwijs.dashboard.web.DashboardWebSession;
import nl.topicus.onderwijs.dashboard.web.components.bargraph.BarGraphPanel;
import nl.topicus.onderwijs.dashboard.web.components.statustable.StatusTablePanel;
import nl.topicus.onderwijs.dashboard.web.resources.ResourceLocator;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.odlabs.wiquery.core.commons.IWiQueryPlugin;
import org.odlabs.wiquery.core.commons.WiQueryResourceManager;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;

public class DashboardPage extends WebPage implements IWiQueryPlugin {

	private static final long serialVersionUID = 1L;

	public DashboardPage(final PageParameters parameters) {
		AjaxLink<Void> liveToRandomModeSwitch = new AjaxLink<Void>("live") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				((DashboardWebSession) getSession()).switchMode();
				target.addComponent(this);
			}
		};
		add(liveToRandomModeSwitch);
		liveToRandomModeSwitch.add(new Label("label",
				new PropertyModel<String>(getSession(), "mode")));
		add(new BarGraphPanel("bargraph"));
		add(new StatusTablePanel("table"));
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(ResourceLocator.class,
				"jquery.timers-1.1.3.js");
		manager.addJavaScriptResource(DashboardPage.class,
				"jquery.dashboardclock.js");
	}

	@Override
	public JsStatement statement() {
		return new JsQuery(this).$().chain("dashboardClock");
	}
}
