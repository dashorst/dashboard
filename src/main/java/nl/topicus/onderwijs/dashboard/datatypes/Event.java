package nl.topicus.onderwijs.dashboard.datatypes;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import nl.topicus.onderwijs.dashboard.keys.Key;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Event implements Serializable {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"dd-MM-yyyy");
	private static final long serialVersionUID = 1L;
	private Key key;
	private String title;
	private Date dateTime;
	private boolean major;
	private Set<String> tags = new TreeSet<String>();
	private String color;

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getKeyName() {
		return getKey().getName();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getDaysUntil() {
		Calendar nowDate = Calendar.getInstance();
		nowDate.set(Calendar.MILLISECOND, 0);
		nowDate.set(Calendar.SECOND, 0);
		nowDate.set(Calendar.MINUTE, 0);
		nowDate.set(Calendar.HOUR_OF_DAY, 0);

		Calendar eventDate = Calendar.getInstance();
		eventDate.setTime(getDateTime());
		eventDate.set(Calendar.MILLISECOND, 0);
		eventDate.set(Calendar.SECOND, 0);
		eventDate.set(Calendar.MINUTE, 0);
		eventDate.set(Calendar.HOUR_OF_DAY, 0);

		long diffInMs = eventDate.getTimeInMillis() - nowDate.getTimeInMillis();
		return (int) (diffInMs / (24 * 3600 * 1000));
	}

	public String getDaysUntilAsString() {
		int days = getDaysUntil();
		if (days == 0)
			return "Today";
		if (days == 1)
			return "Tomorrow";
		if (days < 7) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, days);
			return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,
					Locale.getDefault());
		}
		return days + " days until";
	}

	public String getDateAsString() {
		return DATE_FORMAT.format(getDateTime());
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public boolean isMajor() {
		return major;
	}

	public void setMajor(boolean major) {
		this.major = major;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
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
}
