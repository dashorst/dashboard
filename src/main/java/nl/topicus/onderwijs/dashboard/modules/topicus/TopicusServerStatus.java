package nl.topicus.onderwijs.dashboard.modules.topicus;

import java.io.Serializable;
import java.io.StringWriter;

import nl.topicus.onderwijs.dashboard.datatypes.DotColor;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

class TopicusServerStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	private String version;
	private int numberOfUsers;
	private int numberOfErrors;
	private long uptime;
	private DotColor serverStatus;
	private Integer requestsPerMinute;
	private Integer averageRequestDuration;
	private String url;
	private String code;

	public TopicusServerStatus(String code, String url) {
		this.code = code;
		this.url = url;
	}

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

	public int getNumberOfErrors() {
		return numberOfErrors;
	}

	public void setNumberOfErrors(int numberOfErrors) {
		this.numberOfErrors = numberOfErrors;
	}

	public Integer getAverageRequestDuration() {
		return averageRequestDuration;
	}

	public void setAverageRequestDuration(int averageRequestDuration) {
		this.averageRequestDuration = averageRequestDuration;
	}

	public Integer getRequestsPerMinute() {
		return requestsPerMinute;
	}

	public void setRequestsPerMinute(Integer requestsPerMinute) {
		this.requestsPerMinute = requestsPerMinute;
	}

	public Long getUptime() {
		return uptime;
	}

	public void setUptime(Long uptime) {
		this.uptime = uptime;
	}

	public DotColor getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(DotColor serverStatus) {
		this.serverStatus = serverStatus;
	}

	public String getCode() {
		return code;
	}

	public String getUrl() {
		return url;
	}

	public boolean isOnline() {
		return getServerStatus() == DotColor.GREEN;
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
