package nl.topicus.onderwijs.dashboard.modules.summary;

import java.util.ArrayList;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Alerts;
import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

public class AlertSumImpl implements Alerts {
	public AlertSumImpl() {
	}

	@Override
	public List<Alert> getValue() {
		List<Alert> ret = new ArrayList<Alert>();
		for (Alerts curAlerts : WicketApplication.get().getRepository()
				.getData(Alerts.class).values()) {
			if (curAlerts instanceof AlertSumImpl)
				continue;
			List<Alert> newAlerts = curAlerts.getValue();
			if (newAlerts != null)
				ret.addAll(newAlerts);
		}
		return ret;
	}

}
