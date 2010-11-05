package nl.topicus.onderwijs.dashboard.modules.mantis;

import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MantisUpdateTask implements Runnable {
	private static final Logger log = LoggerFactory
			.getLogger(MantisUpdateTask.class);

	private final WicketApplication application;
	private final MantisService retriever;

	public MantisUpdateTask(WicketApplication application, Repository repository) {
		this.application = application;
		retriever = new MantisService();
		retriever.onConfigure(repository);
	}

	@Override
	public void run() {
		log.info("Mantis updates start");
		if (!application.isShuttingDown()) {
			retriever.refreshData();
		} else {
			log.info("Cancelled refresh due to application shutting down");
		}
		log.info("Mantis updates complete");
	}
}
