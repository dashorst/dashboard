package nl.topicus.onderwijs.dashboard.modules.topicus;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.datatypes.DotColor;

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
	private List<DotColor> serverStatusses;
	private List<Alert> alerts;

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

	public void setServerStatusses(List<DotColor> serverStatusses) {
		this.serverStatusses = serverStatusses;
	}

	public List<DotColor> getServerStatusses() {
		return serverStatusses;
	}

	public List<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
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
