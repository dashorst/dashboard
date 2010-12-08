package nl.topicus.onderwijs.dashboard.modules.topicus;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.datatypes.Dot;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

class TopicusApplicationStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Alert> alerts;
	private List<TopicusServerStatus> servers = new ArrayList<TopicusServerStatus>();

	public TopicusApplicationStatus() {
	}

	public void addServer(TopicusServerStatus server) {
		servers.add(server);
	}

	public List<TopicusServerStatus> getServers() {
		return servers;
	}

	public List<TopicusServerStatus> getOnlineServers() {
		List<TopicusServerStatus> ret = new ArrayList<TopicusServerStatus>();
		for (TopicusServerStatus curServer : getServers()) {
			if (curServer.isOnline())
				ret.add(curServer);
		}
		return ret;
	}

	public String getVersion() {
		String ret = null;
		for (TopicusServerStatus curServer : getServers()) {
			if (curServer.getVersion() != null) {
				if (ret == null || ret.compareTo(curServer.getVersion()) > 0)
					ret = curServer.getVersion();
			}
		}
		return ret == null ? "n/a" : ret;
	}

	public int getNumberOfUsers() {
		int ret = 0;
		for (TopicusServerStatus curServer : getOnlineServers()) {
			ret += curServer.getNumberOfUsers();
		}
		return ret;
	}

	public List<Integer> getUsersPerServer() {
		List<Integer> ret = new ArrayList<Integer>();
		for (TopicusServerStatus curServer : getServers()) {
			ret.add(curServer.getNumberOfUsers());
		}
		return ret;
	}

	public int getNumberOfErrors() {
		int ret = 0;
		for (TopicusServerStatus curServer : getServers()) {
			ret += curServer.getNumberOfErrors();
		}
		return ret;
	}

	public int getNumberOfServers() {
		return servers.size();
	}

	public int getNumberOfServersOnline() {
		return getOnlineServers().size();
	}

	public Integer getAverageRequestDuration() {
		int div = 0;
		int total = 0;
		for (TopicusServerStatus curServer : getOnlineServers()) {
			Integer rpm = curServer.getRequestsPerMinute();
			Integer ard = curServer.getAverageRequestDuration();
			if (rpm != null && ard != null) {
				div += rpm;
				total += (rpm * ard);
			}
		}
		return div == 0 ? null : total / div;
	}

	public Integer getRequestsPerMinute() {
		Integer total = null;
		for (TopicusServerStatus curServer : getOnlineServers()) {
			if (curServer.getRequestsPerMinute() != null) {
				if (total == null)
					total = 0;
				total += curServer.getRequestsPerMinute();
			}
		}
		return total;
	}

	public Long getUptime() {
		Long ret = null;
		for (TopicusServerStatus curServer : getOnlineServers()) {
			if (curServer.getUptime() != null) {
				if (ret == null || curServer.getUptime() < ret)
					ret = curServer.getUptime();
			}
		}
		return ret;
	}

	public List<Dot> getServerStatusses() {
		List<Dot> ret = new ArrayList<Dot>();
		for (TopicusServerStatus curServer : getServers()) {
			ret.add(new Dot(curServer.getServerStatus(), curServer.getCode()));
		}
		return ret;
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
