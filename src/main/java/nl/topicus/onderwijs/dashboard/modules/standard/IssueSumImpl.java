package nl.topicus.onderwijs.dashboard.modules.standard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Issues;
import nl.topicus.onderwijs.dashboard.datatypes.Issue;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

public class IssueSumImpl implements Issues {
	public IssueSumImpl() {
	}

	@Override
	public List<Issue> getValue() {
		List<Issue> ret = new ArrayList<Issue>();
		DashboardRepository repository = WicketApplication.get().getRepository();
		for (Key curKey : repository.getKeys(Key.class)) {
			Collection<DataSource<?>> dataSources = repository.getData(curKey);
			for (DataSource<?> curDataSource : dataSources) {
				if (curDataSource instanceof IssueSumImpl)
					continue;
				if (curDataSource instanceof Issues) {
					List<Issue> newIssues = ((Issues) curDataSource).getValue();
					if (newIssues != null)
						ret.addAll(newIssues);
				}
			}
		}
		return ret;
	}

}
