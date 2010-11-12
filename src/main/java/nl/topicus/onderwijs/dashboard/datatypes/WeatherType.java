package nl.topicus.onderwijs.dashboard.datatypes;

public enum WeatherType {
	CLEAR(0),

	FEW_CLOUDS(1, 10),

	PARTLY_CLOUDY(2, 20),

	CHANCE_SHOWERS(80),

	CHANCE_THUNDERSTORM(95),

	OVERCAST(3, 30),

	HAZE(4, 40, 45, 48, 49),

	MIST(5, 50, 51, 53, 55),

	FREEZING_DRIZZLE(56, 57),

	RAIN(6, 63, 65),

	LIGHT_RAIN(60, 61),

	FREEZING_RAIN(66, 67),

	RAIN_SNOW(68, 69, 83, 84),

	SNOW(7, 73, 85, 86),

	LIGHT_SNOW(70, 71),

	FLURRIES(75),

	SHOWERS(8, 81, 82),

	THUNDERSTORM(9, 90, 96),

	NOT_AVAILABLE(999),

	UNKNOWN;

	private int[] codes;

	WeatherType(int... codes) {
		this.codes = codes;
	}

	public int[] getCodes() {
		return codes;
	}

	public static WeatherType findType(int code) {
		for (WeatherType curType : values()) {
			for (int curCode : curType.getCodes()) {
				if (curCode == code)
					return curType;
			}
		}
		return UNKNOWN;
	}
}
