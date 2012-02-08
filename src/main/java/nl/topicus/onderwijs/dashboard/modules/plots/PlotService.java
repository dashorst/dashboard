package nl.topicus.onderwijs.dashboard.modules.plots;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import org.apache.commons.math.ArgumentOutsideDomainException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ServiceConfiguration(interval = 1, unit = TimeUnit.MINUTES, runInRandomMode = true)
public class PlotService extends AbstractService {
	private Map<PlotKey, DataSourceSeries<?, ?>> data = new HashMap<PlotKey, DataSourceSeries<?, ?>>();
	private Map<PlotKey, DataSourcePlotSeries<?, ?>> series = new HashMap<PlotKey, DataSourcePlotSeries<?, ?>>();

	private WicketApplication application;

	private LoessInterpolator loessInterpolator;

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
		loessInterpolator = new LoessInterpolator();
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
		PlotKey key = new PlotKey(project, dataSource);
		data.put(key, new DataSourceSeries<T, D>(project, dataSource));
		series.put(key, new DataSourcePlotSeries<T, D>(project));
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

	@Override
	public void refreshData() {
		cleanupDataEntries();

		updateDataEntries();

		updateAllPlotSeries();
	}

	private void cleanupDataEntries() {
		Map<Key, Map<String, ?>> serviceSettings = getSettings()
				.getServiceSettings(PlotService.class);
		for (DataSourceSeries<?, ?> curData : data.values()) {
			if (serviceSettings.containsKey(curData.getKey())
					&& serviceSettings.get(curData.getKey()).containsKey(
							"timeToLive")) {
				int dataTTL = Integer.parseInt(serviceSettings
						.get(curData.getKey()).get("timeToLive").toString());
				Calendar ttlDate = Calendar.getInstance();
				ttlDate.add(Calendar.SECOND, 0 - dataTTL);
				curData.cleanupEntries(ttlDate.getTime());
			}
		}
	}

	private void updateDataEntries() {
		for (DataSourceSeries<?, ?> curData : data.values()) {
			curData.addEntry(application.getRepository());
		}
	}

	private void updateAllPlotSeries() {
		for (DataSourceSeries<?, ?> curData : data.values()) {
			DataSourcePlotSeries<Integer, ?> curSeries = (DataSourcePlotSeries<Integer, ?>) series
					.get(new PlotKey(curData.getKey(), curData.getDataSource()));
			updatePlotSeries(curSeries, curData);
		}
	}

	private void updatePlotSeries(DataSourcePlotSeries<Integer, ?> curSeries,
			DataSourceSeries<?, ?> curData) {
		if (!updatePlotSeriesWithLoessInterpolatorData(curSeries, curData)) {
			updatePlotSeriesWithOriginalData(curSeries, curData);
		}
	}

	private void updatePlotSeriesWithOriginalData(
			DataSourcePlotSeries<Integer, ?> curSeries,
			DataSourceSeries<?, ?> curData) {
		curSeries.clear();
		for (DataSourceSeriesEntry<?> entry : curData.getData()) {
			curSeries.addEntry(entry.getKey(), (Integer) entry.getValue());
		}
	}

	private boolean updatePlotSeriesWithLoessInterpolatorData(
			DataSourcePlotSeries<Integer, ?> curSeries,
			DataSourceSeries<?, ?> curData) {
		if (curData.getData().size() < 10) {
			return false;
		}

		Date last = curData.getLastEntry().getKey();
		double[] xvals = new double[curData.getData().size()];
		double[] yvals = new double[curData.getData().size()];
		for (int i = 0; i < curData.getData().size(); i++) {
			xvals[i] = new Long(curData.getData().get(i).getKey().getTime());
			if (curData.getData().get(i).getValue() != null) {
				yvals[i] = curData.getData().get(i).getValue().doubleValue();
			}
		}

		PolynomialSplineFunction psf = null;
		try {
			psf = loessInterpolator.interpolate(xvals, yvals);
		} catch (MathException e) {
			e.printStackTrace();
			return false;
		}
		if (psf != null) {
			curSeries.clear();
			Date time = curData.getFirstEntry().getKey();
			do {
				try {
					double v = psf.value(time.getTime());
					curSeries.addEntry(time, Double.valueOf(v).intValue());

				} catch (ArgumentOutsideDomainException e) {
					e.printStackTrace();
					return false;
				}
				Calendar c = Calendar.getInstance();
				c.setTime(time);
				c.add(Calendar.SECOND, 10);
				time = c.getTime();
			} while (time.before(last));
			return true;
		}
		return false;
	}
}