package nl.topicus.onderwijs.dashboard.web.components;

import java.io.IOException;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonResourceBehavior<T> extends AbstractDefaultAjaxBehavior {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(JsonResourceBehavior.class);

	private IModel<T> model;

	public JsonResourceBehavior(IModel<T> model) {
		this.model = model;
	}

	@Override
	protected void respond(AjaxRequestTarget target) {
		RequestCycle cycle = RequestCycle.get();
		cycle.scheduleRequestHandlerAfterCurrent(new IRequestHandler() {
			@Override
			public void detach(IRequestCycle requestCycle) {
			}

			@Override
			public void respond(IRequestCycle requestCycle) {
				WebResponse r = (WebResponse) requestCycle.getResponse();

				// Determine encoding
				final String encoding = Application.get()
						.getRequestCycleSettings().getResponseRequestEncoding();
				r.setContentType("application/json; charset=" + encoding);

				// Make sure it is not cached by a
				r.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
				r.setHeader("Cache-Control", "no-cache, must-revalidate");
				r.setHeader("Pragma", "no-cache");

				ObjectMapper mapper = new ObjectMapper();
				try {
					mapper.writeValue(r.getOutputStream(), model.getObject());
				} catch (JsonGenerationException e) {
					log.error("Unable to serialize value", e);
				} catch (JsonMappingException e) {
					log.error("Unable to serialize value", e);
				} catch (IOException e) {
					log.error("Unable to serialize value", e);
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
