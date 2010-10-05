package nl.topicus.onderwijs.dashboard.web.components.statustable;

import java.util.HashMap;
import java.util.Map;

public class ColumnData {
	private String label;
	private Map<String, Object> data = new HashMap<String, Object>();

	public ColumnData() {
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}
