package nl.topicus.onderwijs.dashboard.web;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;

public class DashboardWebSession extends WebSession {
	private static final long serialVersionUID = 1L;

	private DashboardMode mode = DashboardMode.RandomData;

	public DashboardWebSession(Request request) {
		super(request);
	}

	public DashboardMode getMode() {
		return mode;
	}

	public void switchMode() {
		mode = mode.switchToOtherMode();
	}

	public static DashboardWebSession get() {
		return (DashboardWebSession) Session.get();
	}
}
