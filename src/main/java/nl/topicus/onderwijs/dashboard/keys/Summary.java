package nl.topicus.onderwijs.dashboard.keys;

import org.codehaus.jackson.annotate.JsonCreator;

public class Summary extends Misc {
	private static final long serialVersionUID = 1L;

	private static final Summary INSTANCE = new Summary();

	private Summary() {
		super("summary", "Summary");
	}

	@JsonCreator
	public static Summary get() {
		return INSTANCE;
	}
}
