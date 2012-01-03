package nl.topicus.onderwijs.dashboard.web.components.bargraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.topicus.onderwijs.dashboard.keys.Project;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.odlabs.wiquery.core.IWiQueryPlugin;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.commons.WiQueryUIPlugin;
import org.odlabs.wiquery.ui.widget.WidgetJavaScriptResourceReference;

@WiQueryUIPlugin
public class BarGraphPanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;
	private IModel<List<Class<? extends DataSource<? extends Number>>>> dataSources;

	public BarGraphPanel(
			String id,
			final IModel<List<Class<? extends DataSource<? extends Number>>>> dataSources) {
		super(id);
		this.dataSources = dataSources;
		ListView<Project> bars = new ListView<Project>("bars",
				WicketApplication.get().getProjects()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Project> item) {
				item.add(new BarGraphBarPanel("bar", item.getModel(),
						dataSources));
			}
		};
		add(bars);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.renderJavaScriptReference(WidgetJavaScriptResourceReference
				.get());
		response.renderJavaScriptReference(new JavaScriptResourceReference(
				BarGraphBarPanel.class, "jquery.ui.dashboardbargraphmaster.js"));
	}

	@Override
	public JsStatement statement() {
		ObjectMapper mapper = new ObjectMapper();
		List<BarDataSet> dataSets = new ArrayList<BarDataSet>();

		int count = 1;
		for (Class<? extends DataSource<?>> curDataSource : dataSources
				.getObject()) {
			dataSets.add(new BarDataSet(curDataSource, "color-" + count));
			count++;
		}

		Options options = new Options();
		options.put("secondsBetweenSwitch", WicketApplication.get()
				.isDevelopment() ? 30 : 60);
		try {
			options.put("dataSets", mapper.writeValueAsString(dataSets));
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardBarGraphMaster",
				options.getJavaScriptOptions());
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		dataSources.detach();
	}
}
