package nl.topicus.onderwijs.dashboard.modules.topicus;

import java.util.ArrayList;
import java.util.List;

import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicusProjectsUpdateTask implements Runnable {
	private static final Logger log = LoggerFactory
			.getLogger(TopicusProjectsUpdateTask.class);

	private final WicketApplication application;

	private List<Retriever> retrievers = new ArrayList<Retriever>();

	public TopicusProjectsUpdateTask(WicketApplication application,
			Repository repo) {
		this.application = application;

		retrievers.add(new VocusStatusRetriever());
		retrievers.add(new VocusOuderportaalRetriever());
		retrievers.add(new ParnassysStatusRetriever());

		for (Retriever retriever : retrievers) {
			retriever.onConfigure(repo);
		}
	}

	@Override
	public void run() {
		log.info("Topicus Project updates start");
		for (Retriever retriever : retrievers) {
			if (!application.isShuttingDown()) {
				retriever.refreshData();
			} else {
				log.info("Cancelled refresh due to application shutting down");
			}
		}
		log.info("Topicus Project updates complete");
	}
}
