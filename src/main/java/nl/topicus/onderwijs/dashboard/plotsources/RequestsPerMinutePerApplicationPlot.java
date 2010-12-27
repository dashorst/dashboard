package nl.topicus.onderwijs.dashboard.plotsources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.RequestsPerMinute;
import nl.topicus.onderwijs.dashboard.modules.PlotSource;
import nl.topicus.onderwijs.dashboard.modules.plots.DataSourcePlotSeries;
import nl.topicus.onderwijs.dashboard.modules.plots.PlotService;
import nl.topicus.wqplot.components.JQPlot;
import nl.topicus.wqplot.options.PlotOptions;

import org.apache.wicket.model.util.ListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestsPerMinutePerApplicationPlot extends
		AbstractPlotSource<Integer, RequestsPerMinute> implements PlotSource {
	private PlotService plotService;

	@Autowired
	public RequestsPerMinutePerApplicationPlot(PlotService plotService) {
		this.plotService = plotService;
	}

	@Override
	public JQPlot createPlot(String id) {
		List<DataSourcePlotSeries<Integer, RequestsPerMinute>> series = plotService
				.getSeries(RequestsPerMinute.class);
		JQPlot ret = new JQPlot(
				id,
				new ListModel<DataSourcePlotSeries<Integer, RequestsPerMinute>>(
						series));
		PlotOptions options = ret.getOptions();
		options.getTitle().setText("Requests per minute");

		setDefaultOptions(options);
		setSeriesLabels(options, series);
		setAxisMinAndMax(options, series, 500);
		return ret;
	}
}
