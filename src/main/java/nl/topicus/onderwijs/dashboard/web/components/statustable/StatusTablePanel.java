package nl.topicus.onderwijs.dashboard.web.components.statustable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import nl.topicus.onderwijs.dashboard.datasources.ApplicationVersion;
import nl.topicus.onderwijs.dashboard.datasources.AverageRequestTime;
import nl.topicus.onderwijs.dashboard.datasources.HudsonBuildNumber;
import nl.topicus.onderwijs.dashboard.datasources.HudsonBuildStatus;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfServers;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfServersOffline;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUnitTests;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.datasources.ProjectAlerts;
import nl.topicus.onderwijs.dashboard.datasources.RequestsPerMinute;
import nl.topicus.onderwijs.dashboard.datasources.ServerStatus;
import nl.topicus.onderwijs.dashboard.datasources.Uptime;
import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.datatypes.DotColor;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.components.JsonResourceBehavior;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.util.ListModel;
import org.odlabs.wiquery.core.commons.IWiQueryPlugin;
import org.odlabs.wiquery.core.commons.WiQueryResourceManager;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.commons.WiQueryUIPlugin;
import org.odlabs.wiquery.ui.widget.WidgetJavascriptResourceReference;

@WiQueryUIPlugin
public class StatusTablePanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer projects;

	private JsonResourceBehavior<Map<String, DotColor>> dataResource;

	public StatusTablePanel(String id) {
		super(id);

		this.dataResource = new JsonResourceBehavior<Map<String, DotColor>>(
				new AbstractReadOnlyModel<Map<String, DotColor>>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Map<String, DotColor> getObject() {
						Map<String, DotColor> ret = new HashMap<String, DotColor>();
						retrieveDataFromApplication(ret);
						return ret;
					}
				});
		add(dataResource);

		projects = new WebMarkupContainer("projects");
		add(projects);

		final Map<String, List<Class<? extends DataSource<?>>>> columns = new TreeMap<String, List<Class<? extends DataSource<?>>>>();
		List<Class<? extends DataSource<?>>> sources1 = new ArrayList<Class<? extends DataSource<?>>>();
		sources1.add(NumberOfUsers.class);
		sources1.add(RequestsPerMinute.class);
		sources1.add(AverageRequestTime.class);

		List<Class<? extends DataSource<?>>> sources2 = new ArrayList<Class<? extends DataSource<?>>>();
		sources2.add(ApplicationVersion.class);
		sources2.add(Uptime.class);

		List<Class<? extends DataSource<?>>> sources3 = new ArrayList<Class<? extends DataSource<?>>>();
		sources3.add(HudsonBuildStatus.class);
		sources3.add(NumberOfUnitTests.class);
		sources3.add(HudsonBuildNumber.class);

		List<Class<? extends DataSource<?>>> sources4 = new ArrayList<Class<? extends DataSource<?>>>();
		sources4.add(NumberOfServers.class);
		sources4.add(ServerStatus.class);
		sources4.add(NumberOfServersOffline.class);

		columns.put("color-1", sources1);
		columns.put("color-2", sources2);
		columns.put("color-3", sources3);
		columns.put("color-4", sources4);

		ListView<String> columnsView = new ListView<String>("columns",
				new ArrayList<String>(columns.keySet())) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<String> item) {
				item.add(new StatusTableColumnPanel("column", item.getModel(),
						new ListModel<Class<? extends DataSource<?>>>(columns
								.get(item.getModelObject()))));
			}
		};
		add(columnsView);
	}

	private void retrieveDataFromApplication(Map<String, DotColor> ret) {
		for (Project curProject : WicketApplication.get().getProjects()) {
			ProjectAlerts alerts = WicketApplication.get().getRepository()
					.getData(ProjectAlerts.class).get(curProject);
			DotColor max = null;
			for (Alert curAlert : alerts.getValue()) {
				if (DotColor.RED.equals(curAlert.getColor())) {
					max = DotColor.RED;
					break;
				} else if (DotColor.YELLOW.equals(curAlert.getColor()))
					max = DotColor.YELLOW;
			}
			if (max != null) {
				ret.put(curProject.getCode(), max);
			}
		}
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(StatusTablePanel.class,
				"jquery.ui.dashboardstatustablemaster.js");
	}

	@Override
	public JsStatement statement() {
		Options projectList = new Options();
		for (Project project : WicketApplication.get().getProjects()) {
			projectList.putLiteral(project.getCode(), project.getName());
		}

		Options options = new Options();
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		options.put("projects", projectList.getJavaScriptOptions().toString());
		options.put("secondsBetweenScroll", WicketApplication.get()
				.isDevelopment() ? 15 : 30);
		options.put("secondsBetweenRotate", WicketApplication.get()
				.isDevelopment() ? 5 : 10);
		JsQuery jsq = new JsQuery(projects);
		return jsq.$().chain("dashboardStatusTableMaster",
				options.getJavaScriptOptions());
	}
}
