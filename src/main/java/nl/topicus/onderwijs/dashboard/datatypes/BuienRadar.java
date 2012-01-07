package nl.topicus.onderwijs.dashboard.datatypes;

import java.io.Serializable;

public class BuienRadar implements Serializable {
	private static final long serialVersionUID = 1L;

	// 5 minute intervals, covering the next hour
	private Integer[] rainForecast = new Integer[12];

	public BuienRadar() {
	}

	public Integer[] getRainForecast() {
		return rainForecast;
	}

	public void setRainForecast(Integer[] rainForecast) {
		this.rainForecast = rainForecast;
	}
}
