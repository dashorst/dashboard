package nl.topicus.onderwijs.dashboard.modules.hudson;

import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HudsonUpdateTask implements Runnable {
	private static final Logger log = LoggerFactory
			.getLogger(HudsonUpdateTask.class);

	private final WicketApplication application;
	private final HudsonService retriever;

	public HudsonUpdateTask(WicketApplication application) {
		this.application = application;
		retriever = new HudsonService();
		retriever.onConfigure(application.getRepository());
	}

	@Override
	public void run() {
		log.info("Hudson updates start");
		if (!application.isShuttingDown()) {
			retriever.refreshData();
		} else {
			log.info("Cancelled refresh due to application shutting down");
		}
		log.info("Hudson updates complete");
	}
}
