package nl.topicus.onderwijs.dashboard.keys;

import org.codehaus.jackson.annotate.JsonProperty;

public abstract class AbstractCodeNameKey implements Key {
	private static final long serialVersionUID = 1L;
	private String code;
	private String name;
	private String color;

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

	public String getColor() {
		return color;
	}

	@JsonProperty("color")
	public void setColor(String color) {
		this.color = color;
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
