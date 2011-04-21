package nl.topicus.onderwijs.dashboard.modules.plots;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import nl.topicus.onderwijs.dashboard.config.ISettings;
import nl.topicus.onderwijs.dashboard.datasources.AverageRequestTime;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.datasources.RequestsPerMinute;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.keys.Project;
import nl.topicus.onderwijs.dashboard.modules.AbstractService;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.ServiceConfiguration;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ServiceConfiguration(interval = 1, unit = TimeUnit.MINUTES, runInRandomMode = true)
public class PlotService extends AbstractService {
	private Map<PlotKey, DataSourcePlotSeries<?, ?>> series = new HashMap<PlotKey, DataSourcePlotSeries<?, ?>>();

	private WicketApplication application;

	@Autowired
	public PlotService(ISettings settings) {
		super(settings);
	}

	@Autowired
	public void setApplication(WicketApplication application) {
		this.application = application;
	}

	@Override
	public void onConfigure(DashboardRepository repository) {
		for (Project curProject : repository.getKeys(Project.class)) {
			if (repository.getData(AverageRequestTime.class).containsKey(
					curProject))
				addSeries(curProject, AverageRequestTime.class);
			if (repository.getData(NumberOfUsers.class).containsKey(curProject))
				addSeries(curProject, NumberOfUsers.class);
			if (repository.getData(RequestsPerMinute.class).containsKey(
					curProject))
				addSeries(curProject, RequestsPerMinute.class);
		}
	}

	private <T extends Number, D extends DataSource<T>> void addSeries(
			Project project, Class<D> dataSource) {
		series.put(new PlotKey(project, dataSource),
				new DataSourcePlotSeries<T, D>(project, dataSource));
	}

	@Override
	public void refreshData() {
		Map<Key, Map<String, ?>> serviceSettings = getSettings()
				.getServiceSettings(PlotService.class);

		for (DataSourcePlotSeries<?, ?> curSeries : series.values()) {
			if (serviceSettings.containsKey(curSeries.getKey())
					&& serviceSettings.get(curSeries.getKey()).containsKey(
							"timeToLive")) {
				int dataTTL = Integer.parseInt(serviceSettings
						.get(curSeries.getKey()).get("timeToLive").toString());
				Calendar ttlDate = Calendar.getInstance();
				ttlDate.add(Calendar.SECOND, 0 - dataTTL);

				curSeries.cleanupEntries(ttlDate.getTime());
			}
			curSeries.addEntry(application.getRepository());
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Number, D extends DataSource<T>> List<DataSourcePlotSeries<T, D>> getSeries(
			Class<D> dataSource) {
		List<DataSourcePlotSeries<T, D>> ret = new ArrayList<DataSourcePlotSeries<T, D>>();
		for (Project curProject : application.getRepository().getKeys(
				Project.class)) {
			ret.add((DataSourcePlotSeries<T, D>) series.get(new PlotKey(
					curProject, dataSource)));
		}
		return ret;
	}
}