package nl.topicus.onderwijs.dashboard.web;

import java.util.Date;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Commits;
import nl.topicus.onderwijs.dashboard.datasources.Events;
import nl.topicus.onderwijs.dashboard.datasources.Issues;
import nl.topicus.onderwijs.dashboard.datasources.ProjectAlerts;
import nl.topicus.onderwijs.dashboard.keys.Project;
import nl.topicus.onderwijs.dashboard.keys.Summary;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.modules.RandomDataRepositoryImpl;
import nl.topicus.onderwijs.dashboard.modules.RepositoryImpl;
import nl.topicus.onderwijs.dashboard.modules.standard.AlertSumImpl;
import nl.topicus.onderwijs.dashboard.modules.standard.CommitSumImpl;
import nl.topicus.onderwijs.dashboard.modules.standard.EventSumImpl;
import nl.topicus.onderwijs.dashboard.modules.standard.IssueSumImpl;
import nl.topicus.onderwijs.dashboard.modules.standard.ProjectAlertImpl;
import nl.topicus.onderwijs.dashboard.timers.Updater;
import nl.topicus.onderwijs.dashboard.web.components.resource.StartTimeResource;
import nl.topicus.onderwijs.dashboard.web.pages.DashboardPage;

import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.session.ISessionStore;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see nl.topicus.onderwijs.dashboard.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
	private static final Logger log = LoggerFactory
			.getLogger(WicketApplication.class);
	@Autowired
	private Updater updater;

	@Autowired
	private RepositoryImpl repository;

	@Autowired
	private RandomDataRepositoryImpl randomRepository;

	private boolean terminated;
	private Date startTime = new Date();
	private DashboardMode mode;

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

		getMarkupSettings().setStripWicketTags(true);
		getSharedResources().putClassAlias(Application.class, "application");
		getSharedResources().add("starttime", new StartTimeResource());
		getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);

		getRequestLoggerSettings().setRequestLoggerEnabled(false);
		getRequestLoggerSettings().setRequestsWindowSize(200);
		getRequestLoggerSettings().setRecordSessionSize(true);

		if (isDevelopment()) {
			mode = DashboardMode.RandomData;
		} else {
			mode = DashboardMode.LiveData;
		}

		randomRepository.addDataSource(Summary.get(), ProjectAlerts.class,
				new AlertSumImpl());
		randomRepository.addDataSource(Summary.get(), Events.class,
				new EventSumImpl());
		randomRepository.addDataSource(Summary.get(), Issues.class,
				new IssueSumImpl());
		randomRepository.addDataSource(Summary.get(), Commits.class,
				new CommitSumImpl());
		for (Project curProject : repository.getProjects()) {
			randomRepository.addDataSource(curProject, ProjectAlerts.class,
					new ProjectAlertImpl(curProject));
		}
	}

	@Override
	protected ISessionStore newSessionStore() {
		return new HttpSessionStore(this);
	}

	public List<Project> getProjects() {
		return getRepository().getProjects();
	}

	public synchronized void enableLiveUpdater() {
		updater.start();
	}

	public synchronized void disableLiveUpdater() {
		updater.stop();
	}

	public boolean isUpdaterEnabled() {
		return updater.isEnabled();
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

	public DashboardRepository getRepository() {
		if (getMode() == DashboardMode.RandomData)
			return randomRepository;
		return repository;
	}

	public DashboardMode getMode() {
		return mode;
	}

	public void switchMode() {
		mode = mode.switchToOtherMode();
		switch (mode) {
		case LiveData:
			WicketApplication.get().enableLiveUpdater();
			break;
		case RandomData:
			WicketApplication.get().disableLiveUpdater();
		}
	}

	public boolean isShuttingDown() {
		return terminated;
	}

	public Date getStartTime() {
		return startTime;
	}
}
