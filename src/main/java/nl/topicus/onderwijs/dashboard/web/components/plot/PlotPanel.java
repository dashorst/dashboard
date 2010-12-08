package nl.topicus.onderwijs.dashboard.web.components.plot;

import nl.topicus.onderwijs.dashboard.modules.PlotSource;
import nl.topicus.wqplot.components.JQPlot;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.odlabs.wiquery.core.commons.IWiQueryPlugin;
import org.odlabs.wiquery.core.commons.WiQueryResourceManager;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.commons.WiQueryUIPlugin;
import org.odlabs.wiquery.ui.widget.WidgetJavascriptResourceReference;

@WiQueryUIPlugin
public class PlotPanel extends Panel implements IWiQueryPlugin {
	private class PlotUpdateBehavior extends AbstractDefaultAjaxBehavior {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			target.addComponent(plot);
		}

		@Override
		public CharSequence getCallbackScript() {
			return "function() {" + super.getCallbackScript() + "}";
		}
	}

	private static final long serialVersionUID = 1L;

	private PlotUpdateBehavior updateBehavior;
	private JQPlot plot;

	public PlotPanel(String id, PlotSource plotSource) {
		super(id);
		updateBehavior = new PlotUpdateBehavior();
		add(updateBehavior);
		add(plot = plotSource.createPlot("plot"));
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(PlotPanel.class,
				"jquery.ui.dashboardplot.js");
	}

	@Override
	public JsStatement statement() {
		Options options = new Options();
		options.put("callback", updateBehavior.getCallbackScript().toString());
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardPlot", options.getJavaScriptOptions());
	}
}
