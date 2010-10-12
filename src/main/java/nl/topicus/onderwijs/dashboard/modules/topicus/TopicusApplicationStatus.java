package nl.topicus.onderwijs.dashboard.modules.topicus;

import java.io.Serializable;
import java.io.StringWriter;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

class TopicusApplicationStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	private String version;
	private int numberOfUsers;
	private int numberOfRequests;
	private int numberOfErrors;
	private int numberOfServers;
	private int numberOfServersOnline;
	private int averageRequestDuration;
	private long uptime;

	public String getVersion() {
		return version == null ? "n/a" : version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(int numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	public int getNumberOfRequests() {
		return numberOfRequests;
	}

	public void setNumberOfRequests(int numberOfRequests) {
		this.numberOfRequests = numberOfRequests;
	}

	public int getNumberOfErrors() {
		return numberOfErrors;
	}

	public void setNumberOfErrors(int numberOfErrors) {
		this.numberOfErrors = numberOfErrors;
	}

	public int getNumberOfServers() {
		return numberOfServers;
	}

	public void setNumberOfServers(int numberOfServers) {
		this.numberOfServers = numberOfServers;
	}

	public int getNumberOfServersOnline() {
		return numberOfServersOnline;
	}

	public void setNumberOfServersOnline(int numberOfServersOnline) {
		this.numberOfServersOnline = numberOfServersOnline;
	}

	public int getAverageRequestDuration() {
		return averageRequestDuration;
	}

	public void setAverageRequestDuration(int averageRequestDuration) {
		this.averageRequestDuration = averageRequestDuration;
	}

	public Long getUptime() {
		return uptime;
	}

	public void setUptime(Long uptime) {
		this.uptime = uptime;
	}

	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		try {
			SerializationConfig config = mapper.getSerializationConfig();
			config.setSerializationInclusion(Inclusion.NON_NULL);
			mapper.writeValue(sw, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sw.toString();
	}
}
