package nl.topicus.onderwijs.dashboard.timers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.modules.google.GoogleUpdateTask;
import nl.topicus.onderwijs.dashboard.modules.hudson.HudsonUpdateTask;
import nl.topicus.onderwijs.dashboard.modules.ns.NSUpdateTask;
import nl.topicus.onderwijs.dashboard.modules.topicus.TopicusProjectsUpdateTask;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Updater {
	private static final Logger log = LoggerFactory.getLogger(Updater.class);
	private ScheduledExecutorService timer;
	private WicketApplication application;
	private ScheduledFuture<?> slowScheduledFuture;
	private ScheduledFuture<?> fastScheduledFuture;

	public Updater(WicketApplication application, Repository repository) {
		this.application = application;

		timer = Executors.newScheduledThreadPool(2);

		log.info("Scheduling timer tasks");
		List<Runnable> slowtasks = Arrays.asList(
				//
				new HudsonUpdateTask(application, repository),
				new GoogleUpdateTask(application, repository),
				new NSUpdateTask(application, repository));
		List<Runnable> fasttasks = Arrays.<Runnable> asList(
		//
				new TopicusProjectsUpdateTask(application, repository));
		slowScheduledFuture = timer.scheduleWithFixedDelay(new TimerTask(
				slowtasks), 0, 60, TimeUnit.SECONDS);
		fastScheduledFuture = timer.scheduleWithFixedDelay(new TimerTask(
				fasttasks), 0, 30, TimeUnit.SECONDS);
	}

	public void stop() {
		log.info("Stopping timer task");
		slowScheduledFuture.cancel(true);
		fastScheduledFuture.cancel(true);
		timer.shutdownNow();
		try {
			boolean terminated = timer.awaitTermination(10, TimeUnit.SECONDS);
			if (!terminated) {
				log.error("Failed to terminate the timer within 10 seconds");
			}
		} catch (InterruptedException e) {
			log.info("Terminating the timer was interrupted", e);
		}
	}

	class TimerTask implements Runnable {
		private List<Runnable> tasks;

		public TimerTask(List<Runnable> tasks) {
			this.tasks = tasks;
		}

		@Override
		public void run() {
			for (Runnable task : tasks) {
				try {
					if (application.isUpdaterEnabled()
							&& !application.isShuttingDown()) {
						task.run();
					}
				} catch (Exception e) {
					log.error("Uncaught exception during update task "
							+ task.getClass().getSimpleName() + ": "
							+ e.getClass().getSimpleName() + ": "
							+ e.getMessage());
				}
			}
		}
	}
}
