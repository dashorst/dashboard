package nl.topicus.onderwijs.dashboard.web.pages;

import nl.topicus.onderwijs.dashboard.web.DashboardWebSession;
import nl.topicus.onderwijs.dashboard.web.components.bargraph.BarGraphPanel;
import nl.topicus.onderwijs.dashboard.web.components.statustable.StatusTablePanel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

public class DashboardPage extends WebPage {

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
}
