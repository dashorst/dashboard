package nl.topicus.onderwijs.dashboard.modules;

public abstract class AbstractCodeNameKey implements Key {
	private static final long serialVersionUID = 1L;
	private String code;
	private String name;

	public AbstractCodeNameKey(String code) {
		this.code = code;
	}

	public AbstractCodeNameKey(String code, String name) {
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
		if (obj != null && obj.getClass().equals(getClass()))
			return ((AbstractCodeNameKey) obj).getCode().equals(getCode());
		return false;
	}

	@Override
	public int hashCode() {
		return getCode().hashCode();
	}

	@Override
	public String toString() {
		return code;
	}
}
