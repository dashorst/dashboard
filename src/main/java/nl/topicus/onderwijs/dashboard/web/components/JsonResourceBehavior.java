package nl.topicus.onderwijs.dashboard.web.components;

import java.io.IOException;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonResourceBehavior<T> extends AbstractDefaultAjaxBehavior {
	private static final long serialVersionUID = 1L;

	private IModel<T> model;

	public JsonResourceBehavior(IModel<T> model) {
		this.model = model;
	}

	@Override
	protected void respond(AjaxRequestTarget target) {
		RequestCycle cycle = RequestCycle.get();
		cycle.setRequestTarget(new IRequestTarget() {
			@Override
			public void detach(RequestCycle requestCycle) {
			}

			@Override
			public void respond(RequestCycle requestCycle) {
				WebResponse r = (WebResponse) requestCycle.getResponse();

				// Determine encoding
				final String encoding = Application.get()
						.getRequestCycleSettings().getResponseRequestEncoding();
				r.setCharacterEncoding(encoding);
				r.setContentType("text/xml; charset=" + encoding);

				// Make sure it is not cached by a
				r.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
				r.setHeader("Cache-Control", "no-cache, must-revalidate");
				r.setHeader("Pragma", "no-cache");

				ObjectMapper mapper = new ObjectMapper();
				try {
					mapper.writeValue(r.getOutputStream(), model.getObject());
				} catch (JsonGenerationException e) {
				} catch (JsonMappingException e) {
				} catch (IOException e) {
				}
			}
		});
	}

	@Override
	public void detach(Component component) {
		super.detach(component);
		model.detach();
	}
}
