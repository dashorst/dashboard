package nl.topicus.onderwijs.dashboard.modules.summary;

import nl.topicus.onderwijs.dashboard.datasources.Alerts;
import nl.topicus.onderwijs.dashboard.modules.Keys;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.modules.topicus.Retriever;

public class SummaryRetriever implements Retriever {

	@Override
	public void onConfigure(Repository repository) {
		repository
				.addDataSource(Keys.SUMMARY, Alerts.class, new AlertSumImpl());
	}

	@Override
	public void refreshData() {
		// Noop
	}
}
