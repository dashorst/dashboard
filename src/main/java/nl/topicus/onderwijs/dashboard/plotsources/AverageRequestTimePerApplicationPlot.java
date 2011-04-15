package nl.topicus.onderwijs.dashboard.plotsources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.AverageRequestTime;
import nl.topicus.onderwijs.dashboard.modules.PlotSource;
import nl.topicus.onderwijs.dashboard.modules.plots.DataSourcePlotSeries;
import nl.topicus.onderwijs.dashboard.modules.plots.PlotService;
import nl.topicus.wqplot.components.JQPlot;
import nl.topicus.wqplot.options.PlotOptions;

import org.apache.wicket.model.util.ListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AverageRequestTimePerApplicationPlot extends
		AbstractPlotSource<Integer, AverageRequestTime> implements PlotSource {
	private PlotService plotService;

	@Autowired
	public AverageRequestTimePerApplicationPlot(PlotService plotService) {
		this.plotService = plotService;
	}

	@Override
	public JQPlot createPlot(String id) {
		List<DataSourcePlotSeries<Integer, AverageRequestTime>> series = plotService
				.getSeries(AverageRequestTime.class);
		JQPlot ret = new JQPlot(
				id,
				new ListModel<DataSourcePlotSeries<Integer, AverageRequestTime>>(
						series));
		PlotOptions options = ret.getOptions();
		options.getTitle().setText("Average request time (ms)");

		setDefaultOptions(options);
		setSeriesLabels(options, series);
		setAxisMinAndMax(options, series, 50);
		options.getAxes().getYaxis().setRenderer("$.jqplot.LogAxisRenderer");
		return ret;
	}
}
