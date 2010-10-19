package nl.topicus.onderwijs.dashboard.web.components.resource;

import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.apache.wicket.markup.html.DynamicWebResource;

public class StartTimeResource extends DynamicWebResource {
	private static final long serialVersionUID = 1L;

	@Override
	protected ResourceState getResourceState() {
		return new ResourceState() {

			@Override
			public String getContentType() {
				return "text/javascript";
			}

			@Override
			public byte[] getData() {
				return ('"' + WicketApplication.get().getStartTime().toString() + '"')
						.getBytes();
			}
		};
	}
}
