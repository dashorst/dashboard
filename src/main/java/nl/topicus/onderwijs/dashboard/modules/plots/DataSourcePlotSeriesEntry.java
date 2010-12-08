package nl.topicus.onderwijs.dashboard.modules.plots;

import java.io.Serializable;
import java.util.Date;

import nl.topicus.wqplot.data.AbstractSeriesEntry;

public class DataSourcePlotSeriesEntry<T extends Number> extends
		AbstractSeriesEntry<Date, T> implements Serializable {
	private static final long serialVersionUID = 1L;

	public DataSourcePlotSeriesEntry(Date key, T value) {
		super(key, value);
	}

}
