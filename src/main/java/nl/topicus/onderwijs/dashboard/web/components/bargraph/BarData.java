package nl.topicus.onderwijs.dashboard.web.components.bargraph;

public class BarData {
	private double height;
	private String value;

	public BarData(double height, String value) {
		this.height = height;
		this.value = value;
	}

	public double getHeight() {
		return height;
	}

	public String getValue() {
		return value;
	}
}
