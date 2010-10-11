package nl.topicus.onderwijs.dashboard.web.components.bargraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.topicus.TopicusApplicationStatus;
import nl.topicus.onderwijs.dashboard.web.DashboardMode;
import nl.topicus.onderwijs.dashboard.web.DashboardWebSession;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.components.JsonResourceBehavior;
import nl.topicus.onderwijs.dashboard.web.components.statustable.StatusTablePanel;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.odlabs.wiquery.core.commons.IWiQueryPlugin;
import org.odlabs.wiquery.core.commons.WiQueryResourceManager;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.commons.WiQueryUIPlugin;
import org.odlabs.wiquery.ui.widget.WidgetJavascriptResourceReference;

@WiQueryUIPlugin
public class BarGraphBarPanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;
	private JsonResourceBehavior<Map<String, BarData>> dataResource;
	private IModel<List<String>> dataSetsModel;

	public BarGraphBarPanel(String id, IModel<Project> projectModel,
			IModel<List<String>> dataSetsModel) {
		super(id, projectModel);
		this.dataSetsModel = dataSetsModel;
		this.dataResource = new JsonResourceBehavior<Map<String, BarData>>(
				new AbstractReadOnlyModel<Map<String, BarData>>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Map<String, BarData> getObject() {
						if (DashboardWebSession.get().getMode() == DashboardMode.RandomData) {
							return generateRandomValues();
						}
						return retrieveDataFromApplication();
					}
				});
		add(dataResource);
	}

	protected Map<String, BarData> retrieveDataFromApplication() {
		Map<String, BarData> ret = new HashMap<String, BarData>();
		getLiveSessionsData(ret, "livesessions");
		getNumberOfServers(ret, "numberofservers");
		return ret;
	}

	private void getLiveSessionsData(Map<String, BarData> ret, String key) {
		Project project = getProject();
		Map<Project, TopicusApplicationStatus> statusses = WicketApplication
				.get().getStatusses();

		int max = 0;
		for (TopicusApplicationStatus status : statusses.values()) {
			Integer numberOfServers = status.getNumberOfServers();
			max = Math.max(numberOfServers == null ? 0 : numberOfServers, max);
		}
		TopicusApplicationStatus status = statusses.get(project);
		status.getNumberOfUsers();

		double value = (10.0 * status.getNumberOfServers()) / max;
		ret.put(key,
				new BarData(value,
						Integer.toString(status.getNumberOfServers())));
	}

	private void getNumberOfServers(Map<String, BarData> ret, String key) {
		Project project = getProject();
		Map<Project, TopicusApplicationStatus> statusses = WicketApplication
				.get().getStatusses();

		int max = 0;
		for (TopicusApplicationStatus status : statusses.values()) {
			Integer numberOfUsers = status.getNumberOfUsers();
			max = Math.max(numberOfUsers == null ? 0 : numberOfUsers, max);
		}
		TopicusApplicationStatus status = statusses.get(project);
		status.getNumberOfUsers();

		double value = (10.0 * status.getNumberOfUsers()) / max;
		ret.put(key,
				new BarData(value, Integer.toString(status.getNumberOfUsers())));
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(StatusTablePanel.class,
				"jquery.timers-1.1.3.js");
		manager.addJavaScriptResource(BarGraphBarPanel.class,
				"jquery.ui.dashboardbargraph.js");
	}

	public Project getProject() {
		return (Project) getDefaultModelObject();
	}

	@Override
	public JsStatement statement() {
		Options options = new Options();
		options.putLiteral("projectName", getProject().getName());
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardBarGraph",
				options.getJavaScriptOptions());
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		dataSetsModel.detach();
	}

	private Map<String, BarData> generateRandomValues() {
		Map<String, BarData> ret = new HashMap<String, BarData>();
		int index = 0;
		for (String curDataSet : BarGraphBarPanel.this.dataSetsModel
				.getObject()) {
			index++;
			double value = Math.random() * 10.0;
			ret.put(curDataSet,
					new BarData(value, Long.toString(Math.round(value * 10)
							* index)));
		}
		return ret;
	}
}
