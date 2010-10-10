package nl.topicus.onderwijs.dashboard.web;

import java.util.Map;

import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.topicus.TopicusApplicationStatus;
import nl.topicus.onderwijs.dashboard.timers.Updater;
import nl.topicus.onderwijs.dashboard.web.pages.DashboardPage;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see nl.topicus.onderwijs.dashboard.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
	private Updater updater;
	private Map<Project, TopicusApplicationStatus> statusses;

	@Override
	public Class<DashboardPage> getHomePage() {
		return DashboardPage.class;
	}

	@Override
	protected void init() {
		super.init();

		updater = new Updater(this);
		getMarkupSettings().setStripWicketTags(true);
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new DashboardWebSession(request);
	}

	public static WicketApplication get() {
		return (WicketApplication) WebApplication.get();
	}

	public synchronized void updateStatusses(
			Map<Project, TopicusApplicationStatus> statusses) {
		this.statusses = statusses;
	}

	public synchronized Map<Project, TopicusApplicationStatus> getStatusses() {
		return statusses;
	}
}
