package nl.topicus.onderwijs.dashboard.web;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;

public class DashboardWebSession extends WebSession {
	private static final long serialVersionUID = 1L;

	public DashboardWebSession(Request request) {
		super(request);
	}

	public static DashboardWebSession get() {
		return (DashboardWebSession) Session.get();
	}
}
