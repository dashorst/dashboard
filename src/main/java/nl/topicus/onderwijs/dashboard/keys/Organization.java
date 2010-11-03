package nl.topicus.onderwijs.dashboard.keys;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class Organization extends AbstractCodeNameKey {
	private static final long serialVersionUID = 1L;

	public Organization(String code, String name) {
		super(code, name);
	}

	@JsonCreator
	public static Organization from(@JsonProperty("code") String code,
			@JsonProperty("name") String name) {
		return new Organization(code, name);
	}
}
