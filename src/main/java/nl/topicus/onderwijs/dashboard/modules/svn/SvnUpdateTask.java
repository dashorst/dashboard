package nl.topicus.onderwijs.dashboard.modules.svn;

import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SvnUpdateTask implements Runnable {
	private static final Logger log = LoggerFactory
			.getLogger(SvnUpdateTask.class);

	private final WicketApplication application;
	private final SvnService retriever;

	public SvnUpdateTask(WicketApplication application, Repository repository) {
		this.application = application;
		retriever = new SvnService();
		retriever.onConfigure(repository);
	}

	@Override
	public void run() {
		log.info("Svn updates start");
		if (!application.isShuttingDown()) {
			retriever.refreshData();
		} else {
			log.info("Cancelled refresh due to application shutting down");
		}
		log.info("Svn updates complete");
	}
}
