package nl.topicus.onderwijs.dashboard.web;

import java.util.Date;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Events;
import nl.topicus.onderwijs.dashboard.datasources.ProjectAlerts;
import nl.topicus.onderwijs.dashboard.modules.Keys;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.RandomDataRepositoryImpl;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.modules.RepositoryImpl;
import nl.topicus.onderwijs.dashboard.modules.Settings;
import nl.topicus.onderwijs.dashboard.modules.standard.AlertSumImpl;
import nl.topicus.onderwijs.dashboard.modules.standard.EventSumImpl;
import nl.topicus.onderwijs.dashboard.modules.standard.ProjectAlertImpl;
import nl.topicus.onderwijs.dashboard.persistence.config.ConfigurationRepository;
import nl.topicus.onderwijs.dashboard.timers.Updater;
import nl.topicus.onderwijs.dashboard.web.components.resource.StartTimeResource;
import nl.topicus.onderwijs.dashboard.web.pages.DashboardPage;

import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see nl.topicus.onderwijs.dashboard.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
	private static final Logger log = LoggerFactory
			.getLogger(WicketApplication.class);
	private Updater updater;

	private RepositoryImpl repository = new RepositoryImpl();

	private RandomDataRepositoryImpl randomRepository = new RandomDataRepositoryImpl(
			repository);
	private boolean terminated;
	private Date startTime = new Date();

	@Override
	public Class<DashboardPage> getHomePage() {
		return DashboardPage.class;
	}

	@Override
	protected void onDestroy() {
		terminated = true;
		log.info("Shutting down the dashboard application");
		disableLiveUpdater();
		randomRepository.stop();
		log.info("Shutting down the dashboard application, finished");
	}

	@Override
	protected void init() {
		super.init();

		ConfigurationRepository config = new ConfigurationRepository();
		log.info("Checking for existing dashboard configuration");
		if (!config.configurationExists(Settings.class)) {

			log
					.info("No existing dashboard configuration found, generating a default one");
			Keys.generateDefaultConfiguration();
		}
		getMarkupSettings().setStripWicketTags(true);
		getSharedResources().putClassAlias(Application.class, "application");
		getSharedResources().add("starttime", new StartTimeResource());

		randomRepository.addDataSource(Keys.SUMMARY, ProjectAlerts.class,
				new AlertSumImpl());
		randomRepository.addDataSource(Keys.SUMMARY, Events.class,
				new EventSumImpl());
		for (Project curProject : repository.getProjects()) {
			randomRepository.addDataSource(curProject, ProjectAlerts.class,
					new ProjectAlertImpl(curProject));
		}
	}

	public List<Project> getProjects() {
		return getRepository().getProjects();
	}

	public synchronized void enableLiveUpdater() {
		if (updater == null)
			updater = new Updater(this, randomRepository);
	}

	public synchronized void disableLiveUpdater() {
		if (updater != null) {
			updater.stop();
			updater = null;
		}
	}

	public boolean isUpdaterEnabled() {
		return updater != null;
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new DashboardWebSession(request);
	}

	public static WicketApplication get() {
		return (WicketApplication) WebApplication.get();
	}

	public boolean isDevelopment() {
		return Application.DEVELOPMENT.equals(getConfigurationType());
	}

	public Repository getRepository() {
		if (DashboardWebSession.get().getMode() == DashboardMode.RandomData)
			return randomRepository;
		return repository;
	}

	public boolean isShuttingDown() {
		return terminated;
	}

	public Date getStartTime() {
		return startTime;
	}
}
