package nl.topicus.onderwijs.dashboard.web;

public enum DashboardMode {
	LiveData, RandomData;

	public DashboardMode switchToOtherMode() {
		return values()[(this.ordinal() + 1) % values().length];
	}
}
