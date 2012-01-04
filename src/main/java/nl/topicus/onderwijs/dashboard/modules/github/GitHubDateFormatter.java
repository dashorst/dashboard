package nl.topicus.onderwijs.dashboard.modules.github;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.egit.github.core.client.DateFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GitHubDateFormatter extends DateFormatter {
	private final DateFormat formatWithTZ = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssX");

	@Override
	public Date deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		try {
			return formatWithTZ.parse(json.getAsString());
		} catch (ParseException e) {
			return super.deserialize(json, typeOfT, context);
		}
	}
}
