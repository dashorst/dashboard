package nl.topicus.onderwijs.dashboard.modules.topicus;

import nl.topicus.onderwijs.dashboard.modules.Repository;

public interface Retriever {
	public void onConfigure(Repository repository);

	public void refreshData();
}
