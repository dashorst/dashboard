package nl.topicus.onderwijs.dashboard.keys;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class User extends AbstractCodeNameKey {
	private static final long serialVersionUID = 1L;

	public User(String code, String name) {
		super(code, name);
	}

	@JsonCreator
	public static User from(@JsonProperty("code") String code,
			@JsonProperty("name") String name) {
		return new User(code, name);
	}
}
