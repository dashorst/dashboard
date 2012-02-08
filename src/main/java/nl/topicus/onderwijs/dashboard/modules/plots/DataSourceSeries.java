package nl.topicus.onderwijs.dashboard.modules.plots;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.LastServerCheckTime;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.modules.DataSource;

public class DataSourceSeries<T extends Number, D extends DataSource<T>>
		implements Serializable {
	private static final long serialVersionUID = 1L;
	private Key key;
	private Class<D> dataSource;
	private LinkedList<DataSourceSeriesEntry<T>> data = new LinkedList<DataSourceSeriesEntry<T>>();

	public DataSourceSeries(Key key, Class<D> dataSource) {
		this.key = key;
		this.dataSource = dataSource;
	}

	public Key getKey() {
		return key;
	}

	public Class<D> getDataSource() {
		return dataSource;
	}

	public List<DataSourceSeriesEntry<T>> getData() {
		return Collections.unmodifiableList(data);
	}

	public DataSourceSeriesEntry<T> getFirstEntry() {
		return data.getFirst();
	}

	public DataSourceSeriesEntry<T> getLastEntry() {
		return data.isEmpty() ? null : data.getLast();
	}

	public void addEntry(DashboardRepository repository) {
		LastServerCheckTime time = repository
				.getData(LastServerCheckTime.class).get(key);
		Date timeValue = time == null ? null : time.getValue();
		if (timeValue == null)
			return;

		D source = repository.getData(dataSource).get(key);
		T value = source == null ? null : source.getValue();

		if ((data.isEmpty() || data.getLast().getKey().before(timeValue))
				&& value != null)
			data.add(new DataSourceSeriesEntry<T>(timeValue, value));
	}

	/**
	 * Removes any data with date older than ttlDate.
	 * 
	 * @param ttlDate
	 */
	public void cleanupEntries(Date ttlDate) {
		Iterator<DataSourceSeriesEntry<T>> iter = data.iterator();
		while (iter.hasNext()) {
			DataSourceSeriesEntry<T> entry = iter.next();
			if (entry.getKey().before(ttlDate))
				iter.remove();
			else
				return; // stop if date is after ttlDate, the series is ordered
						// chronologically.
		}
	}
}
