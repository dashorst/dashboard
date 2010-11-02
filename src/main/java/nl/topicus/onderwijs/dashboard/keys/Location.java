package nl.topicus.onderwijs.dashboard.keys;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A location is a physical location of any kind. This can be used for
 * information such as train departures, traffic jams, weather information, etc.
 */
public class Location extends AbstractCodeNameKey {
	private static final long serialVersionUID = 1L;

	public Location(String code, String name) {
		super(code, name);
	}

	@JsonCreator
	public static Location from(@JsonProperty("code") String code,
			@JsonProperty("name") String name) {
		return new Location(code, name);
	}
}
