package nl.topicus.onderwijs.dashboard.modules.standard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Alerts;
import nl.topicus.onderwijs.dashboard.datasources.ProjectAlerts;
import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

public class ProjectAlertImpl implements ProjectAlerts {
	private Project project;

	public ProjectAlertImpl(Project project) {
		this.project = project;
	}

	@Override
	public List<Alert> getValue() {
		List<Alert> ret = new ArrayList<Alert>();
		Repository repository = WicketApplication.get().getRepository();
		Collection<DataSource<?>> dataSources = repository.getData(project);
		for (DataSource<?> curDataSource : dataSources) {
			if (curDataSource instanceof ProjectAlerts)
				continue;

			if (curDataSource instanceof Alerts) {
				List<Alert> newAlerts = ((Alerts) curDataSource).getValue();
				if (newAlerts != null)
					ret.addAll(newAlerts);
			}
		}
		return ret;
	}

}
