package nl.topicus.onderwijs.dashboard.keys;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class Misc extends AbstractCodeNameKey {
	private static final long serialVersionUID = 1L;

	public Misc(String code, String name) {
		super(code, name);
	}

	@JsonCreator
	public static Misc from(@JsonProperty("code") String code,
			@JsonProperty("name") String name) {
		return new Misc(code, name);
	}
}
