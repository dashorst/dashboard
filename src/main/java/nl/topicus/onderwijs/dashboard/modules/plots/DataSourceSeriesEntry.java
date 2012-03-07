package nl.topicus.onderwijs.dashboard.modules.plots;

import java.io.Serializable;
import java.util.Date;

public class DataSourceSeriesEntry<T extends Number> implements Serializable {
	private static final long serialVersionUID = 1L;

	private Date key;

	private T value;

	public DataSourceSeriesEntry(Date key, T value) {
		this.key = key;
		this.value = value;
	}

	public Date getKey() {
		return key;
	}

	public void setKey(Date key) {
		this.key = key;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
