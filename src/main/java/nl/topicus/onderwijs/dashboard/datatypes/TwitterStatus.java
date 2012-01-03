package nl.topicus.onderwijs.dashboard.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nl.topicus.onderwijs.dashboard.keys.Key;
import twitter4j.HashtagEntity;
import twitter4j.Status;

public class TwitterStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	private Key key;
	private Date date;
	private String user;
	private String text;
	private List<String> tags;

	public TwitterStatus(Key key) {
		this.key = key;
	}

	public TwitterStatus(Key key, Status status) {
		this.key = key;
		this.date = status.getCreatedAt();
		this.user = status.getUser().getName();
		this.text = status.getText();
		this.tags = new ArrayList<String>();
		if (status.getHashtagEntities() != null) {
			for (HashtagEntity hash : status.getHashtagEntities()) {
				tags.add(hash.getText());
			}
		}
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getTimeAsString() {
		long minsAgo = System.currentTimeMillis() - getDate().getTime();
		minsAgo /= 60000;
		if (minsAgo == 0)
			return "A few seconds ago";
		if (minsAgo == 1)
			return "A minute ago";
		if (minsAgo < 60)
			return minsAgo + " minutes ago";
		long hoursAgo = minsAgo / 60;
		if (hoursAgo == 1)
			return "An hour ago";
		if (hoursAgo < 24)
			return hoursAgo + " hours ago";
		if (hoursAgo < 48)
			return "Yesterday";
		long daysAgo = hoursAgo / 24;
		if (daysAgo < 7) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, (int) -daysAgo);
			return "Last "
					+ cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,
							Locale.getDefault());
		}
		return daysAgo + " days ago";
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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
