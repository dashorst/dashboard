package nl.topicus.onderwijs.dashboard.timers;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.modules.ServiceConfiguration;
import nl.topicus.onderwijs.dashboard.modules.topicus.Retriever;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Updater implements InitializingBean {
	private static final Logger log = LoggerFactory.getLogger(Updater.class);
	private boolean enabled = false;
	private ScheduledExecutorService timer;

	@Autowired
	private WicketApplication application;

	@Autowired
	private List<Retriever> retrievers;

	@Autowired
	@Resource(name = "random")
	private DashboardRepository repository;

	public Updater() {
	}

	@Override
	public void afterPropertiesSet() {
		for (Retriever curRetriever : retrievers) {
			curRetriever.onConfigure(repository);
		}
		if (!application.isDevelopment())
			start();
	}

	public synchronized void start() {
		if (!isEnabled()) {
			enabled = true;
			timer = Executors.newScheduledThreadPool(4);
			log.info("Scheduling timer tasks");
			for (Retriever curRetriever : retrievers) {
				ServiceConfiguration config = curRetriever.getClass()
						.getAnnotation(ServiceConfiguration.class);
				timer.scheduleWithFixedDelay(new TimerTask(curRetriever), 0,
						config.interval(), config.unit());
			}
		}
	}

	public synchronized void stop() {
		if (isEnabled()) {
			enabled = false;
			log.info("Stopping timer task");
			timer.shutdownNow();
			try {
				boolean terminated = timer.awaitTermination(10,
						TimeUnit.SECONDS);
				if (!terminated) {
					log
							.error("Failed to terminate the timer within 10 seconds");
				}
			} catch (InterruptedException e) {
				log.info("Terminating the timer was interrupted", e);
			}
		}
	}

	class TimerTask implements Runnable {
		private Retriever retriever;

		public TimerTask(Retriever retriever) {
			this.retriever = retriever;
		}

		@Override
		public void run() {
			try {
				if (isEnabled() && !application.isShuttingDown()) {
					log.info("Starting refresh for "
							+ retriever.getClass().getSimpleName());
					retriever.refreshData();
					log.info("Refresh completed for "
							+ retriever.getClass().getSimpleName());
				}
			} catch (Exception e) {
				log.error("Uncaught exception during update task "
						+ retriever.getClass().getSimpleName() + ": "
						+ e.getClass().getSimpleName() + ": " + e.getMessage());
			}
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
}
