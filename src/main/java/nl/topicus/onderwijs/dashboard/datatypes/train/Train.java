package nl.topicus.onderwijs.dashboard.datatypes.train;

import java.io.IOException;
import java.io.Serializable;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Train implements Serializable {
	private static final long serialVersionUID = 1L;
	private String destination;
	private TrainType type;
	private String departureTime;
	private int delay;
	private String platform;
	private boolean platformChange;

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public TrainType getType() {
		return type;
	}

	public void setType(TrainType type) {
		this.type = type;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public boolean isPlatformChange() {
		return platformChange;
	}

	public void setPlatformChange(boolean platformChange) {
		this.platformChange = platformChange;
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

	public String getKey() {
		StringBuilder ret = new StringBuilder();
		if (getDepartureTime().length() < 5) {
			ret.append('0');
		}
		ret.append(getDepartureTime());
		ret.append('-');
		ret.append(getPlatform());
		return ret.toString();
	}
}
