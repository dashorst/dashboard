package nl.topicus.onderwijs.dashboard.modules.standard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.ProjectAlerts;
import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.keys.Project;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

public class AlertSumImpl implements ProjectAlerts {
	public AlertSumImpl() {
	}

	@Override
	public List<Alert> getValue() {
		List<Alert> ret = new ArrayList<Alert>();
		DashboardRepository repository = WicketApplication.get().getRepository();
		for (Project curProject : repository.getProjects()) {
			Collection<DataSource<?>> dataSources = repository
					.getData(curProject);
			for (DataSource<?> curDataSource : dataSources) {
				if (curDataSource instanceof AlertSumImpl)
					continue;
				if (curDataSource instanceof ProjectAlerts) {
					List<Alert> newAlerts = ((ProjectAlerts) curDataSource)
							.getValue();
					if (newAlerts != null)
						ret.addAll(newAlerts);
				}
			}
		}
		return ret;
	}

}
