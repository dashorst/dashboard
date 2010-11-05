package nl.topicus.onderwijs.dashboard.datatypes;

public enum IssueStatus {
	NEW(10), FEEDBACK(20), ACKNOWLEDGED(30), CONFIRMED(40), ASSIGNED(50), RESOLVED(
			80), CLOSED(90);

	private long id;

	IssueStatus(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public static IssueStatus get(long id) {
		for (IssueStatus curStatus : values())
			if (curStatus.getId() == id)
				return curStatus;
		throw new IllegalArgumentException("Unknown id " + id);
	}
}
