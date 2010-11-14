package nl.topicus.onderwijs.dashboard.datatypes;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class WeatherReport implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
			"HH:mm");

	private WeatherType type;

	private int rainfallProbability;

	private double minTemperature;

	private double maxTemperature;

	private int windDirection;

	private double windSpeed;

	private Date sunrise;

	private Date sunset;

	public WeatherReport() {
	}

	public WeatherType getType() {
		return type;
	}

	public void setType(WeatherType type) {
		this.type = type;
	}

	public int getRainfallProbability() {
		return rainfallProbability;
	}

	public void setRainfallProbability(int rainfallProbability) {
		this.rainfallProbability = rainfallProbability;
	}

	public double getMinTemperature() {
		return minTemperature;
	}

	public void setMinTemperature(double minTemperature) {
		this.minTemperature = minTemperature;
	}

	public double getMaxTemperature() {
		return maxTemperature;
	}

	public void setMaxTemperature(double maxTemperature) {
		this.maxTemperature = maxTemperature;
	}

	public int getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(int windDirection) {
		this.windDirection = windDirection;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}

	public boolean isDay() {
		long now = System.currentTimeMillis();
		return sunrise.getTime() <= now && sunset.getTime() >= now;
	}

	public Date getSunrise() {
		return sunrise;
	}

	public String getSunriseTime() {
		return TIME_FORMAT.format(getSunrise());
	}

	public void setSunrise(Date sunrise) {
		this.sunrise = sunrise;
	}

	public Date getSunset() {
		return sunset;
	}

	public String getSunsetTime() {
		return TIME_FORMAT.format(getSunset());
	}

	public void setSunset(Date sunset) {
		this.sunset = sunset;
	}

	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
