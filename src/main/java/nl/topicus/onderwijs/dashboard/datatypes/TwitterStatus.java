package nl.topicus.onderwijs.dashboard.datatypes;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import nl.topicus.onderwijs.dashboard.keys.Key;
import twitter4j.Status;

public class TwitterStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	private Key key;
	private Date date;
	private String text;
	private List<String> tags;

	public TwitterStatus(Key key) {
		this.key = key;
	}

	public TwitterStatus(Key key, Status status) {
		this.key = key;
		this.date = status.getCreatedAt();
		this.text = status.getText();
		if (status.getHashtags() == null)
			this.tags = Collections.emptyList();
		else
			this.tags = Arrays.asList(status.getHashtags());
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}
