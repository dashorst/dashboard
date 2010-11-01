package nl.topicus.onderwijs.dashboard.modules.google;

import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleUpdateTask implements Runnable {
	private static final Logger log = LoggerFactory
			.getLogger(GoogleUpdateTask.class);

	private final WicketApplication application;
	private final GoogleEventService retriever;

	public GoogleUpdateTask(WicketApplication application, Repository repository) {
		this.application = application;
		retriever = new GoogleEventService();
		retriever.onConfigure(repository);
	}

	@Override
	public void run() {
		log.info("Google updates start");
		if (!application.isShuttingDown()) {
			retriever.refreshData();
		} else {
			log.info("Cancelled refresh due to application shutting down");
		}
		log.info("Google updates complete");
	}
}
