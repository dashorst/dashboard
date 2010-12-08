package nl.topicus.onderwijs.dashboard.plotsources;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.plots.DataSourcePlotSeries;
import nl.topicus.wqplot.options.PlotLegendPlacement;
import nl.topicus.wqplot.options.PlotOptions;

public class AbstractPlotSource<T extends Number, D extends DataSource<T>> {
	public void setDefaultOptions(PlotOptions options) {
		options.getTitle().setTextColor("#220000");
		options.getAxesDefaults().getTickOptions().setTextColor("#ffffff");
		options.getSeriesDefaults().getMarkerOptions().setShow(false);
		options.getAxes().getXaxis().setRenderer("$.jqplot.DateAxisRenderer");
		options.getAxes().getXaxis().getTickOptions().setFormatString("%#H:%M");
		options.getAxes().getYaxis().getTickOptions().setFormatString("%d");
		options.getGrid().setBackground("rgba(0,0,0,0)");
		options.getLegend().setShow(true);
		options.getLegend().setBackground("rgba(0,0,0,0.4)");
		options.getLegend().setTextColor("#ffffff");
		options.getLegend().setMarginLeft("10px");
		options.getLegend().setMarginRight("10px");
		options.getLegend().setRowSpacing("0");
		options.getLegend().setPlacement(PlotLegendPlacement.outsideGrid);
	}

	public void setSeriesLabels(PlotOptions options,
			List<DataSourcePlotSeries<T, D>> series) {
		for (DataSourcePlotSeries<T, D> curSeries : series) {
			options.addNewSeries().setLabel(curSeries.getKey().getName());
		}
	}

	public void setAxisMinAndMax(PlotOptions options,
			List<DataSourcePlotSeries<T, D>> series, long step) {
		Date first = null;
		Date last = null;
		T max = null;
		for (DataSourcePlotSeries<T, D> curSeries : series) {
			if (curSeries.getMaxValue() != null) {
				T seriesMax = curSeries.getMaxValue();
				if (max == null || max.doubleValue() < seriesMax.doubleValue())
					max = seriesMax;
			}
			if (!curSeries.getData().isEmpty()) {
				Date seriesStart = curSeries.getData().get(0).getKey();
				if (seriesStart != null) {
					if (first == null || seriesStart.before(first))
						first = seriesStart;
				}
				Date seriesEnd = curSeries.getData().get(
						curSeries.getData().size() - 1).getKey();
				if (seriesEnd != null) {
					if (last == null || seriesEnd.after(last))
						last = seriesEnd;
				}
			}
		}

		options.getAxes().getYaxis().setMin(0);
		if (max == null) {
			options.getAxes().getYaxis().setMax(100);
		} else {
			long maxLong = max.longValue();
			options.getAxes().getYaxis().setMax(
					((maxLong - 1) / step) * step + step);
		}

		if (first == null) {
			first = new Date();
			last = first;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(first);
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) / 30 * 30);
		options.getAxes().getXaxis().setMin(cal.getTime());

		long timeDiff = last.getTime() - first.getTime();
		timeDiff /= 3600000;
		if (timeDiff <= 4)
			options.getAxes().getXaxis().setTickInterval("30 minutes");
		else if (timeDiff <= 8)
			options.getAxes().getXaxis().setTickInterval("1 hour");
		else
			options.getAxes().getXaxis().setTickInterval("2 hours");
	}
}
