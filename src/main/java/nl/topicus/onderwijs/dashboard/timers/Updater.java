package nl.topicus.onderwijs.dashboard.timers;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.apache.wicket.util.time.Duration;

public class Updater {
	private Timer timer;

	public Updater(WicketApplication application) {
		this.timer = new Timer("Dashboard Updater", true);

		List<Project> projects = Arrays.asList(new Project("atvo", "@VO"),
				new Project("atvo_ouders", "@VO Ouderportaal"));

		TopicusProjectsUpdateTask task = new TopicusProjectsUpdateTask(
				application, projects);
		timer.scheduleAtFixedRate(task, 0, Duration.seconds(30)
				.getMilliseconds());
		// timer.schedule(task, 0);
	}
}
