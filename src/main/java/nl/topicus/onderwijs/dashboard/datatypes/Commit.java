package nl.topicus.onderwijs.dashboard.datatypes;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.topicus.onderwijs.dashboard.keys.Key;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.tmatesoft.svn.core.SVNLogEntry;

public class Commit implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm");

	private Key project;
	private Date dateTime;
	private String revision;
	private String message;
	private String author;
	private int filesChanged;
	private String avatarUrl;

	public Commit() {
	}

	public Commit(Key project, RepositoryCommit commit) {
		this.project = project;
		this.dateTime = commit.getCommit().getAuthor().getDate();
		this.revision = commit.getSha().substring(0, 8);
		this.message = commit.getCommit().getMessage();
		this.author = commit.getCommit().getAuthor().getName();
		this.filesChanged = commit.getFiles() == null ? 0 : commit.getFiles()
				.size();
		this.avatarUrl = commit.getAuthor() == null ? "" : commit.getAuthor()
				.getAvatarUrl();
	}

	public Commit(Key project, SVNLogEntry logEntry) {
		this.project = project;
		this.dateTime = logEntry.getDate();
		this.revision = "r" + Long.toString(logEntry.getRevision());
		this.message = logEntry.getMessage();
		this.author = logEntry.getAuthor();
		this.filesChanged = logEntry.getChangedPaths().size();
		this.avatarUrl = "";
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

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
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

	public int getFilesChanged() {
		return filesChanged;
	}

	public void setFilesChanged(int filesChanged) {
		this.filesChanged = filesChanged;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
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
		return Long.toString(Long.MAX_VALUE - getDateTime().getTime());
	}
}
