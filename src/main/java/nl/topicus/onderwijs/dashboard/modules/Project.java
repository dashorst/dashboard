package nl.topicus.onderwijs.dashboard.modules;

public class Project {
	private String code;
	private String name;

	public Project(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Project)
			return ((Project) obj).getCode().equals(getCode());
		return false;
	}

	@Override
	public int hashCode() {
		return getCode().hashCode();
	}

	@Override
	public String toString() {
		return name;
	}
}
