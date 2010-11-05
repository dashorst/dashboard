package nl.topicus.onderwijs.dashboard.datatypes;

public enum IssuePriority {
	NONE(10), LOW(20), NORMAL(30), HIGH(40), URGENT(50), IMMEDIATE(60);

	private long id;

	IssuePriority(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public static IssuePriority get(long id) {
		for (IssuePriority curStatus : values())
			if (curStatus.getId() == id)
				return curStatus;
		throw new IllegalArgumentException("Unknown id " + id);
	}
}
