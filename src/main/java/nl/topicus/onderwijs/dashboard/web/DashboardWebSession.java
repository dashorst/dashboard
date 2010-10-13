package nl.topicus.onderwijs.dashboard.web;

import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;

public class DashboardWebSession extends WebSession {
	private static final long serialVersionUID = 1L;

	private DashboardMode mode;

	public DashboardWebSession(Request request) {
		super(request);

		if (Application.DEPLOYMENT.equals(getApplication()
				.getConfigurationType())) {
			mode = DashboardMode.LiveData;
			WicketApplication.get().enableLiveUpdater();
		} else {
			mode = DashboardMode.RandomData;
			WicketApplication.get().disableLiveUpdater();
		}
	}

	public DashboardMode getMode() {
		return mode;
	}

	public void switchMode() {
		mode = mode.switchToOtherMode();
		switch (mode) {
		case LiveData:
			WicketApplication.get().enableLiveUpdater();
			break;
		case RandomData:
			WicketApplication.get().disableLiveUpdater();
		}
	}

	public static DashboardWebSession get() {
		return (DashboardWebSession) Session.get();
	}
}
