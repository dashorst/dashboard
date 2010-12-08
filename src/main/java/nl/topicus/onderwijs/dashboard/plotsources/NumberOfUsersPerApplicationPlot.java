package nl.topicus.onderwijs.dashboard.plotsources;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.modules.PlotSource;
import nl.topicus.onderwijs.dashboard.modules.plots.DataSourcePlotSeries;
import nl.topicus.onderwijs.dashboard.modules.plots.PlotService;
import nl.topicus.wqplot.components.JQPlot;
import nl.topicus.wqplot.options.PlotOptions;

import org.apache.wicket.model.util.ListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NumberOfUsersPerApplicationPlot extends
		AbstractPlotSource<Integer, NumberOfUsers> implements PlotSource {
	private PlotService plotService;

	@Autowired
	public NumberOfUsersPerApplicationPlot(PlotService plotService) {
		this.plotService = plotService;
	}

	@Override
	public JQPlot createPlot(String id) {
		List<DataSourcePlotSeries<Integer, NumberOfUsers>> series = plotService
				.getSeries(NumberOfUsers.class);
		JQPlot ret = new JQPlot(id,
				new ListModel<DataSourcePlotSeries<Integer, NumberOfUsers>>(
						series));
		PlotOptions options = ret.getOptions();
		options.getTitle().setText("Number of users");

		setDefaultOptions(options);
		setSeriesLabels(options, series);
		setAxisMinAndMax(options, series, 500);
		return ret;
	}
}
