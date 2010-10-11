package nl.topicus.onderwijs.dashboard.timers;

import java.util.Timer;

import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.apache.wicket.util.time.Duration;

public class Updater {
	private Timer timer;

	public Updater(WicketApplication application) {
		this.timer = new Timer("Dashboard Updater", true);

		TopicusProjectsUpdateTask task = new TopicusProjectsUpdateTask(
				application, application.getProjects());
		timer.scheduleAtFixedRate(task, 0, Duration.seconds(30)
				.getMilliseconds());
		// timer.schedule(task, 0);
	}

	public void stop() {
		timer.cancel();
	}
}
