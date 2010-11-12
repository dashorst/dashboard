package nl.topicus.onderwijs.dashboard.modules.wettercom;

import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WetterComUpdateTask implements Runnable {
	private static final Logger log = LoggerFactory
			.getLogger(WetterComUpdateTask.class);

	private final WicketApplication application;
	private final WetterComService retriever;

	public WetterComUpdateTask(WicketApplication application,
			Repository repository) {
		this.application = application;
		retriever = new WetterComService();
		retriever.onConfigure(repository);
	}

	@Override
	public void run() {
		log.info("Wetter.com updates start");
		if (!application.isShuttingDown()) {
			retriever.refreshData();
		} else {
			log.info("Cancelled refresh due to application shutting down");
		}
		log.info("Wetter.com updates complete");
	}
}
