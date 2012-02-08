package nl.topicus.onderwijs.dashboard.modules.plots;

import java.io.Serializable;
import java.util.Date;

import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.wqplot.data.AbstractSeries;

public class DataSourcePlotSeries<T extends Number, D extends DataSource<T>>
		extends AbstractSeries<Date, T, DataSourcePlotSeriesEntry<T>> implements
		Serializable {
	private static final long serialVersionUID = 1L;
	private Key key;
	private T minValue;
	private T maxValue;

	public DataSourcePlotSeries(Key key) {
		this.key = key;
	}

	public void addEntry(Date time, T value) {
		if (value != null) {
			if (minValue == null
					|| minValue.doubleValue() > value.doubleValue())
				minValue = value;
			if (maxValue == null
					|| maxValue.doubleValue() < value.doubleValue())
				maxValue = value;
		}
		addEntry(new DataSourcePlotSeriesEntry<T>(time, value));
	}

	public Key getKey() {
		return key;
	}

	public T getMinValue() {
		return minValue;
	}

	public T getMaxValue() {
		return maxValue;
	}

	public void clear() {
		minValue = null;
		maxValue = null;
		getData().clear();
	}
}
