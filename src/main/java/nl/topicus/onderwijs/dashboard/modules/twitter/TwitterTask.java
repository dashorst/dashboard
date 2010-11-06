package nl.topicus.onderwijs.dashboard.modules.twitter;

import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitterTask implements Runnable {
	private static final Logger log = LoggerFactory
			.getLogger(TwitterTask.class);
	private TwitterService service;
	private WicketApplication application;
	private Repository repository;

	public TwitterTask(WicketApplication application, Repository repository) {
		this.application = application;
		this.repository = repository;
		this.service = new TwitterService();
		this.service.onConfigure(repository);
	}

	@Override
	public void run() {
		log.info("Starting twitter update");
		try {
			if (!application.isShuttingDown()) {
				service.performTwitterUpdate();
				log.info("Finished twitter update");
			} else {
				log.info("Cancelled refresh due to application shutting down");
			}
		} catch (Exception e) {
			log.error("Twitter update failed:", e);
		}
	}
}
