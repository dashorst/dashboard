package nl.topicus.onderwijs.dashboard.modules.topicus;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import nl.topicus.onderwijs.dashboard.modules.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicusProjectsUpdateTask extends TimerTask {
	private static final Logger log = LoggerFactory
			.getLogger(TopicusProjectsUpdateTask.class);

	private List<Retriever> retrievers = new ArrayList<Retriever>();

	public TopicusProjectsUpdateTask(Repository repository) {
		retrievers.add(new VocusStatusRetriever());
		retrievers.add(new VocusOuderportaalRetriever());
		retrievers.add(new ParnassysStatusRetriever());

		for (Retriever retriever : retrievers) {
			retriever.onConfigure(repository);
		}
	}

	@Override
	public void run() {
		log.info("Topicus Project updates start");
		for (Retriever retriever : retrievers) {
			retriever.refreshData();
		}
		log.info("Topicus Project updates complete");
	}
}
