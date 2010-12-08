package nl.topicus.onderwijs.dashboard.modules.plots;

import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.DataSource;

public class PlotKey {
	private Key key;
	private Class<? extends DataSource<?>> dataSource;

	public PlotKey(Key key, Class<? extends DataSource<?>> dataSource) {
		this.key = key;
		this.dataSource = dataSource;
	}

	@Override
	public int hashCode() {
		return key.hashCode() ^ dataSource.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlotKey) {
			PlotKey other = (PlotKey) obj;
			return other.key.equals(key) && other.dataSource.equals(dataSource);
		}
		return false;
	}

	@Override
	public String toString() {
		return key + ":" + dataSource.getSimpleName();
	}
}
