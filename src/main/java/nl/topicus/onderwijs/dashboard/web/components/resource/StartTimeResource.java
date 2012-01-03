package nl.topicus.onderwijs.dashboard.web.components.resource;

import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.apache.wicket.request.resource.ByteArrayResource;

public class StartTimeResource extends ByteArrayResource {
	private static final long serialVersionUID = 1L;

	public StartTimeResource() {
		super("text/javascript", ('"' + WicketApplication.get().getStartTime()
				.toString() + '"').getBytes());
	}
}
