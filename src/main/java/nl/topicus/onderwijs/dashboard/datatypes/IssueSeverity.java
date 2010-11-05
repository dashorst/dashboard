package nl.topicus.onderwijs.dashboard.datatypes;

public enum IssueSeverity {
	FEATURE(10), TRIVIAL(20), TEXT(30), TWEAK(40), MINOR(50), MAJOR(60), CRASH(
			70), BLOCK(80);

	private long id;

	IssueSeverity(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public static IssueSeverity get(long id) {
		for (IssueSeverity curStatus : values())
			if (curStatus.getId() == id)
				return curStatus;
		throw new IllegalArgumentException("Unknown id " + id);
	}
}
