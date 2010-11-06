package nl.topicus.onderwijs.dashboard.web.components.twitter;

import org.apache.wicket.markup.html.panel.Panel;
import org.odlabs.wiquery.core.commons.IWiQueryPlugin;
import org.odlabs.wiquery.core.commons.WiQueryResourceManager;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.commons.WiQueryUIPlugin;
import org.odlabs.wiquery.ui.widget.WidgetJavascriptResourceReference;

@WiQueryUIPlugin
public class TwitterPanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;

	public TwitterPanel(String id) {
		super(id);
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
	}

	@Override
	public JsStatement statement() {
		return null;
	}
}
