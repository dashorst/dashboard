package nl.topicus.onderwijs.dashboard.web.components.statustable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.topicus.TopicusApplicationStatus;
import nl.topicus.onderwijs.dashboard.web.DashboardMode;
import nl.topicus.onderwijs.dashboard.web.DashboardWebSession;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.components.JsonResourceBehavior;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;
import org.odlabs.wiquery.core.commons.IWiQueryPlugin;
import org.odlabs.wiquery.core.commons.WiQueryResourceManager;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.commons.WiQueryUIPlugin;
import org.odlabs.wiquery.ui.widget.WidgetJavascriptResourceReference;

@WiQueryUIPlugin
public class StatusTableColumnPanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;
	private JsonResourceBehavior<List<ColumnData>> dataResource;

	public StatusTableColumnPanel(String id, IModel<String> scheme) {
		super(id, scheme);
		add(new AttributeAppender("class", scheme, " "));
		this.dataResource = new JsonResourceBehavior<List<ColumnData>>(
				new AbstractReadOnlyModel<List<ColumnData>>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<ColumnData> getObject() {
						List<ColumnData> ret = new ArrayList<ColumnData>();
						if (DashboardWebSession.get().getMode() == DashboardMode.RandomData) {
							generateRandomData(ret);
						} else {
							retrieveDataFromApplication(ret);
						}
						return ret;
					}
				});
		add(dataResource);
	}

	protected void retrieveDataFromApplication(List<ColumnData> ret) {
		Map<Project, TopicusApplicationStatus> statusses = WicketApplication
				.get().getStatusses();

		ColumnData usersData = new ColumnData();
		usersData.setLabel("Current users");
		for (Project project : statusses.keySet()) {
			TopicusApplicationStatus status = statusses.get(project);
			usersData.getData().put(project.getCode(),
					status.getNumberOfUsers());
		}
		ret.add(usersData);

		ColumnData versionData = new ColumnData();
		versionData.setLabel("Version");
		for (Project project : statusses.keySet()) {
			TopicusApplicationStatus status = statusses.get(project);
			versionData.getData().put(project.getCode(), status.getVersion());
		}
		ret.add(versionData);

		ColumnData uptimeData = new ColumnData();
		uptimeData.setLabel("Uptime");
		for (Project project : statusses.keySet()) {
			TopicusApplicationStatus status = statusses.get(project);
			uptimeData.getData().put(
					project.getCode(),
					Duration.milliseconds(status.getUptime()).toString(
							new Locale("NL")));
		}
		ret.add(uptimeData);
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(StatusTablePanel.class,
				"jquery.timers-1.1.3.js");
		manager.addJavaScriptResource(StatusTableColumnPanel.class,
				"jquery.ui.dashboardtable.js");
	}

	@Override
	public JsStatement statement() {
		Options options = new Options();
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardTable", options.getJavaScriptOptions());
	}

	private void generateRandomData(List<ColumnData> ret) {
		for (int count = 0; count < 4; count++) {
			ColumnData curData = new ColumnData();
			curData.setLabel("Label-" + count);
			for (String curProject : Arrays.asList("eduarte", "atvo", "duo",
					"passepartout", "test")) {
				curData.getData().put(curProject,
						Math.round(Math.random() * 1000));
			}
			ret.add(curData);
		}
	}
}
