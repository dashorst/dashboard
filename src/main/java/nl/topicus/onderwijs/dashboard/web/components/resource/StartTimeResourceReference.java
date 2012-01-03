package nl.topicus.onderwijs.dashboard.web.components.resource;

import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

public class StartTimeResourceReference extends ResourceReference {
	private static final long serialVersionUID = 1L;

	public StartTimeResourceReference() {
		super(WicketApplication.class, "starttime");
	}

	@Override
	public IResource getResource() {
		return new StartTimeResource();
	}

}
