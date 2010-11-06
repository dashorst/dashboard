package nl.topicus.onderwijs.dashboard.modules.standard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Commits;
import nl.topicus.onderwijs.dashboard.datatypes.Commit;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

public class CommitSumImpl implements Commits {
	public CommitSumImpl() {
	}

	@Override
	public List<Commit> getValue() {
		List<Commit> ret = new ArrayList<Commit>();
		Repository repository = WicketApplication.get().getRepository();
		for (Key curKey : repository.getKeys(Key.class)) {
			Collection<DataSource<?>> dataSources = repository.getData(curKey);
			for (DataSource<?> curDataSource : dataSources) {
				if (curDataSource instanceof CommitSumImpl)
					continue;
				if (curDataSource instanceof Commits) {
					List<Commit> newCommits = ((Commits) curDataSource)
							.getValue();
					if (newCommits != null)
						ret.addAll(newCommits);
				}
			}
		}
		return ret;
	}

}
