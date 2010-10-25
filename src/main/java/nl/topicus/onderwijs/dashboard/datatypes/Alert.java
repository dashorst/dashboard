package nl.topicus.onderwijs.dashboard.datatypes;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.topicus.onderwijs.dashboard.modules.Project;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Alert implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
			"HH:mm");
	private DotColor color;
	private String time;
	private String message;
	private Project project;

	public Alert() {
	}

	public Alert(Alert oldAlert, DotColor color, Project project, String message) {
		this.color = color;
		this.project = project;
		this.message = message;
		if (oldAlert != null && project.equals(oldAlert.getProject())
				&& oldAlert.getMessage().equals(message)) {
			this.time = oldAlert.getTime();
		} else {
			this.time = TIME_FORMAT.format(new Date());
		}
	}

	public DotColor getColor() {
		return color;
	}

	public void setColor(DotColor color) {
		this.color = color;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
		ret.append(getColor().ordinal());
		ret.append('-');
		if (getTime().length() < 5) {
			ret.append('0');
		}
		ret.append(getTime());
		ret.append('-');
		ret.append(getProject().getCode());
		ret.append('-');
		ret.append(getMessage().hashCode());
		return ret.toString();
	}
}
