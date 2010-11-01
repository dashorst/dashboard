package nl.topicus.onderwijs.dashboard.web.pages;

import java.util.ArrayList;

import nl.topicus.onderwijs.dashboard.datasources.Events;
import nl.topicus.onderwijs.dashboard.datasources.HudsonBuildNumber;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUnitTests;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.datasources.ProjectAlerts;
import nl.topicus.onderwijs.dashboard.datasources.Trains;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.Keys;
import nl.topicus.onderwijs.dashboard.web.DashboardWebSession;
import nl.topicus.onderwijs.dashboard.web.components.bargraph.BarGraphPanel;
import nl.topicus.onderwijs.dashboard.web.components.events.EventsPanel;
import nl.topicus.onderwijs.dashboard.web.components.statustable.StatusTablePanel;
import nl.topicus.onderwijs.dashboard.web.components.table.TablePanel;
import nl.topicus.onderwijs.dashboard.web.resources.ResourceLocator;

import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
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

			@Override
			public boolean isVisible() {
				return Application.DEVELOPMENT.equals(getApplication()
						.getConfigurationType());
			}
		};
		add(liveToRandomModeSwitch);
		liveToRandomModeSwitch.add(new Label("label",
				new PropertyModel<String>(getSession(), "mode")));
		ArrayList<Class<? extends DataSource<? extends Number>>> datasources = new ArrayList<Class<? extends DataSource<? extends Number>>>();
		datasources.add(NumberOfUsers.class);
		// datasources.add(NumberOfServers.class);
		datasources.add(HudsonBuildNumber.class);
		datasources.add(NumberOfUnitTests.class);
		add(new BarGraphPanel("bargraph",
				new ListModel<Class<? extends DataSource<? extends Number>>>(
						datasources)));
		add(new StatusTablePanel("table"));
		add(new TablePanel("ns", Trains.class, Keys.NS));
		add(new TablePanel("alerts", ProjectAlerts.class, Keys.SUMMARY));
		add(new EventsPanel("events", Events.class, Keys.SUMMARY));
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
		return new JsQuery(this).$().chain("dashboardClock",
				"'resources/application/starttime'");
	}
}
