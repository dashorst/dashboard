package nl.topicus.onderwijs.dashboard.modules.plots;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.wqplot.data.AbstractSeries;

public class DataSourcePlotSeries<T extends Number, D extends DataSource<T>>
		extends AbstractSeries<Date, T, DataSourcePlotSeriesEntry<T>> implements
		Serializable {
	private static final long serialVersionUID = 1L;
	private Key key;
	private Class<D> dataSource;
	private T minValue;
	private T maxValue;

	public DataSourcePlotSeries(Key key, Class<D> dataSource) {
		this.key = key;
		this.dataSource = dataSource;
	}

	public void addEntry(DashboardRepository repository) {
		D source = repository.getData(dataSource).get(key);
		T value = source == null ? null : source.getValue();
		if (value != null) {
			if (minValue == null
					|| minValue.doubleValue() > value.doubleValue())
				minValue = value;
			if (maxValue == null
					|| maxValue.doubleValue() < value.doubleValue())
				maxValue = value;
		}
		addEntry(new DataSourcePlotSeriesEntry<T>(new Date(), value));
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

	/**
	 * Removes any data with date older than ttlDate.
	 * 
	 * @param ttlDate
	 */
	public void cleanupEntries(Date ttlDate) {
		Iterator<DataSourcePlotSeriesEntry<T>> iter = getData().iterator();
		while (iter.hasNext()) {
			DataSourcePlotSeriesEntry<T> entry = iter.next();
			if (entry.getKey().before(ttlDate))
				iter.remove();
		}
	}
}
