package nl.topicus.onderwijs.dashboard.modules.ns;

import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NSUpdateTask implements Runnable {
	private static final Logger log = LoggerFactory
			.getLogger(NSUpdateTask.class);

	private final WicketApplication application;
	private final NSService retriever;

	public NSUpdateTask(WicketApplication application, Repository repository) {
		this.application = application;
		retriever = new NSService();
		retriever.onConfigure(repository);
	}

	@Override
	public void run() {
		log.info("NS updates start");
		if (!application.isShuttingDown()) {
			retriever.refreshData();
		} else {
			log.info("Cancelled refresh due to application shutting down");
		}
		log.info("NS updates complete");
	}
}
