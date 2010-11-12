package nl.topicus.onderwijs.dashboard.modules.wettercom;

import nl.topicus.onderwijs.dashboard.datasources.Weather;
import nl.topicus.onderwijs.dashboard.datatypes.WeatherReport;
import nl.topicus.onderwijs.dashboard.keys.Key;

public class WeatherImpl implements Weather {
	private WetterComService service;
	private Key key;

	public WeatherImpl(Key key, WetterComService service) {
		this.key = key;
		this.service = service;
	}

	@Override
	public WeatherReport getValue() {
		return service.getWeather(key);
	}
}
