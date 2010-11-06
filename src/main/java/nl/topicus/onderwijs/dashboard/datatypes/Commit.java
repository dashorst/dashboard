package nl.topicus.onderwijs.dashboard.datatypes;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.topicus.onderwijs.dashboard.keys.Key;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.tmatesoft.svn.core.SVNLogEntry;

public class Commit implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm");

	private Key project;
	private Date dateTime;
	private long revision;
	private String message;
	private String author;

	public Commit(Key project, SVNLogEntry logEntry) {
		this.project = project;
		this.dateTime = logEntry.getDate();
		this.revision = logEntry.getRevision();
		this.message = logEntry.getMessage();
		this.author = logEntry.getAuthor();
	}

	public Key getProject() {
		return project;
	}

	public void setProject(Key project) {
		this.project = project;
	}

	public String getProjectName() {
		return getProject().getName();
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

	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
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
		return Long.toString(Long.MAX_VALUE - getRevision());
	}
}
