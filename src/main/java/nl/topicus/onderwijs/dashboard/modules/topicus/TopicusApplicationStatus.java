package nl.topicus.onderwijs.dashboard.modules.topicus;

import java.io.Serializable;
import java.io.StringWriter;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

public class TopicusApplicationStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	private String applicationName;
	private String version;
	private Integer numberOfUsers;
	private Integer numberOfRequests;
	private Integer numberOfErrors;
	private Integer numberOfServers;
	private Integer numberOfServersOnline;
	private Integer averageRequestDuration;
	private Long uptime;

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	public Integer getNumberOfRequests() {
		return numberOfRequests;
	}

	public void setNumberOfRequests(Integer numberOfRequests) {
		this.numberOfRequests = numberOfRequests;
	}

	public Integer getNumberOfErrors() {
		return numberOfErrors;
	}

	public void setNumberOfErrors(Integer numberOfErrors) {
		this.numberOfErrors = numberOfErrors;
	}

	public Integer getNumberOfServers() {
		return numberOfServers;
	}

	public void setNumberOfServers(Integer numberOfServers) {
		this.numberOfServers = numberOfServers;
	}

	public Integer getNumberOfServersOnline() {
		return numberOfServersOnline;
	}

	public void setNumberOfServersOnline(Integer numberOfServersOnline) {
		this.numberOfServersOnline = numberOfServersOnline;
	}

	public Integer getAverageRequestDuration() {
		return averageRequestDuration;
	}

	public void setAverageRequestDuration(Integer averageRequestDuration) {
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
