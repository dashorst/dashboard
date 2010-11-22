package nl.topicus.onderwijs.dashboard.modules.topicus;

import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;

public interface Retriever {
	public void onConfigure(DashboardRepository repository);

	public void refreshData();
}
