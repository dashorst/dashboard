package nl.topicus.onderwijs.dashboard.datatypes;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.topicus.onderwijs.dashboard.keys.Key;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.mantisbt.connect.model.IIssueHeader;

public class Issue implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm");

	private Date dateTime;
	private IssueStatus status;
	private IssuePriority priority;
	private IssueSeverity severity;
	private long id;
	private String summary;
	private Key project;

	public Issue() {
	}

	public Issue(Key project, IIssueHeader issue) {
		this.project = project;
		this.status = IssueStatus.get(issue.getStatus());
		this.priority = IssuePriority.get(issue.getPriority());
		this.severity = IssueSeverity.get(issue.getSeverity());
		this.id = issue.getId();
		this.summary = issue.getSummary();
		this.dateTime = issue.getDateLastUpdated();
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getTime() {
		return TIME_FORMAT.format(getDateTime());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Key getProject() {
		return project;
	}

	public void setProject(Key project) {
		this.project = project;
	}

	public IssueStatus getStatus() {
		return status;
	}

	public void setStatus(IssueStatus status) {
		this.status = status;
	}

	public IssuePriority getPriority() {
		return priority;
	}

	public void setPriority(IssuePriority priority) {
		this.priority = priority;
	}

	public IssueSeverity getSeverity() {
		return severity;
	}

	public void setSeverity(IssueSeverity severity) {
		this.severity = severity;
	}

	public String getProjectName() {
		return getProject().getName();
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
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
		ret.append(Long.MAX_VALUE - getDateTime().getTime());
		ret.append('-');
		ret.append(getProject().getCode());
		ret.append('-');
		ret.append(getId());
		return ret.toString();
	}
}
