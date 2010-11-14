package nl.topicus.onderwijs.dashboard.modules.wettercom;

import java.io.StringReader;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nl.topicus.onderwijs.dashboard.datasources.Weather;
import nl.topicus.onderwijs.dashboard.datatypes.WeatherReport;
import nl.topicus.onderwijs.dashboard.datatypes.WeatherType;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.modules.Settings;
import nl.topicus.onderwijs.dashboard.modules.topicus.Retriever;
import nl.topicus.onderwijs.dashboard.modules.topicus.RetrieverUtils;
import nl.topicus.onderwijs.dashboard.modules.topicus.StatusPageResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class WetterComService implements Retriever {
	private static final Logger log = LoggerFactory
			.getLogger(WetterComService.class);

	private Map<Key, WeatherReport> reports = new ConcurrentHashMap<Key, WeatherReport>();

	@Override
	public void onConfigure(Repository repository) {
		Settings settings = Settings.get();
		for (Key key : settings
				.getKeysWithConfigurationFor(WetterComService.class)) {
			repository.addDataSource(key, Weather.class, new WeatherImpl(key,
					this));
		}
	}

	@Override
	public void refreshData() {
		try {
			Settings settings = Settings.get();
			Map<Key, Map<String, ?>> serviceSettings = settings
					.getServiceSettings(WetterComService.class);
			for (Map.Entry<Key, Map<String, ?>> curSettingEntry : serviceSettings
					.entrySet()) {
				Map<String, ?> wetterSettingsForProject = curSettingEntry
						.getValue();
				String apiKey = wetterSettingsForProject.get("apiKey")
						.toString();
				String applicationName = wetterSettingsForProject.get(
						"applicationName").toString();
				String cityKey = wetterSettingsForProject.get("cityKey")
						.toString();
				double latitude = (Double) wetterSettingsForProject
						.get("latitude");
				double longitude = (Double) wetterSettingsForProject
						.get("longitude");

				String total = applicationName + apiKey + cityKey;

				byte[] thedigest = MessageDigest.getInstance("MD5").digest(
						total.getBytes("UTF-8"));
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < thedigest.length; i++) {
					sb.append(Integer.toString((thedigest[i] & 0xff) + 0x100,
							16).substring(1));
				}

				StatusPageResponse response = RetrieverUtils
						.getStatuspage(String.format("http://api.wetter.com"
								+ "/forecast/weather/city/%s/project/%s/cs/%s",
								cityKey, applicationName, sb.toString()));
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(response
						.getPageContent()));

				Document doc = db.parse(is);
				Element time = findTime(doc);
				WeatherReport report = new WeatherReport();
				report.setType(WeatherType.findType(Integer
						.parseInt(getTextForElement(time, "w"))));
				report.setRainfallProbability(Integer
						.parseInt(getTextForElement(time, "pc")));
				report.setMinTemperature(Double.parseDouble(getTextForElement(
						time, "tn")));
				report.setMaxTemperature(Double.parseDouble(getTextForElement(
						time, "tx")));
				report.setWindDirection(Integer.parseInt(getTextForElement(
						time, "wd")));
				report.setWindSpeed(Double.parseDouble(getTextForElement(time,
						"ws")));
				report.setSunrise(getSunrize(latitude, longitude));
				report.setSunset(getSunset(latitude, longitude));

				reports.put(curSettingEntry.getKey(), report);
			}
		} catch (Exception e) {
			log.error("Unable to refresh data from google: {} {}", e.getClass()
					.getSimpleName(), e.getMessage());
		}
	}

	private Element findTime(Document doc) {
		long curTime = System.currentTimeMillis() / 1000;
		NodeList nodes = doc.getElementsByTagName("time");
		for (int index = 0; index < nodes.getLength(); index++) {
			Element curElement = (Element) nodes.item(index);
			long date = Long.parseLong(getTextForElement(curElement, "d"));
			long period = Long.parseLong(getTextForElement(curElement, "p"));
			if (date != 0 && period != 0) {
				if (date < curTime && (date + period * 3600) > curTime)
					return curElement;
			}
		}
		return null;
	}

	private String getTextForElement(Element root, String child) {
		NodeList subNodes = root.getChildNodes();
		for (int innerIndex = 0; innerIndex < subNodes.getLength(); innerIndex++) {
			Node curSubNode = subNodes.item(innerIndex);
			if (curSubNode.getNodeName().equals(child)) {
				return curSubNode.getTextContent();
			}
		}
		return null;
	}

	private static final double HOUR_IN_MS = 3600 * 1000;

	public static Date getSunrize(double latitude, double longitude) {
		return getSunrizeOrSet(latitude, longitude, true);
	}

	public static Date getSunset(double latitude, double longitude) {
		return getSunrizeOrSet(latitude, longitude, false);
	}

	private static Date getSunrizeOrSet(double latitude, double longitude,
			boolean rising) {
		double zenith = 90.0 + 50.0 / 60.0;
		// 1. first calculate the day of the year
		//        
		// N1 = floor(275 * month / 9)
		// N2 = floor((month + 9) / 12)
		// N3 = (1 + floor((year - 4 * floor(year / 4) + 2) / 3))
		// N = N1 - (N2 * N3) + day - 30
		int N = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

		// 2. convert the longitude to hour value and calculate an approximate
		// time
		//                                                                                                                                                                                                                                             
		// lngHour = longitude / 15
		double lngHour = longitude / 15.0;

		double t;
		// if rising time is desired:
		// t = N + ((6 - lngHour) / 24)
		if (rising)
			t = N + ((6.0 - lngHour) / 24.0);
		else
			t = N + ((18 - lngHour) / 24);

		// 3. calculate the Sun's mean anomaly
		// M = (0.9856 * t) - 3.289
		double M = (0.9856 * t) - 3.289;

		// 4. calculate the Sun's true longitude
		// L = M + (1.916 * sin(M)) + (0.020 * sin(2 * M)) + 282.634
		double L = M + (1.916 * sin(M)) + (0.020 * sin(2 * M)) + 282.634;

		// NOTE: L potentially needs to be adjusted into the range [0,360) by
		// adding/subtracting 360
		L = makeInRange(L, 360.0);

		// 5a. calculate the Sun's right ascension
		// RA = atan(0.91764 * tan(L))
		double RA = atan(0.91764 * tan(L));

		// NOTE: RA potentially needs to be adjusted into the range [0,360) by
		// adding/subtracting 360
		RA = makeInRange(RA, 360.0);

		// 5b. right ascension value needs to be in the same quadrant as L
		// Lquadrant = (floor( L/90)) * 90
		// RAquadrant = (floor(RA/90)) * 90
		// RA = RA + (Lquadrant - RAquadrant)
		double Lquadrant = (Math.floor(L / 90.0)) * 90.0;
		double RAquadrant = (Math.floor(RA / 90.0)) * 90.0;
		RA = RA + (Lquadrant - RAquadrant);

		// 5c. right ascension value needs to be converted into hours
		// RA = RA / 15
		RA = RA / 15.0;

		// 6. calculate the Sun's declination
		// sinDec = 0.39782 * sin(L)
		// cosDec = cos(asin(sinDec))
		double sinDec = 0.39782 * sin(L);
		double cosDec = cos(asin(sinDec));

		// 7a. calculate the Sun's local hour angle
		// cosH = (cos(zenith) - (sinDec * sin(latitude))) / (cosDec *
		// cos(latitude))
		double cosH = (cos(zenith) - (sinDec * sin(latitude)))
				/ (cosDec * cos(latitude));

		// 7b. finish calculating H and convert into hours
		// if if rising time is desired:
		// H = 360 - acos(cosH)
		double H;
		if (rising)
			H = 360.0 - acos(cosH);
		else
			H = acos(cosH);

		// H = H / 15
		H = H / 15.0;

		// 8. calculate local mean time of rising/setting
		//
		// T = H + RA - (0.06571 * t) - 6.622
		double T = H + RA - (0.06571 * t) - 6.622;

		//
		// 9. adjust back to UTC
		//
		// UT = T - lngHour
		double UT = T - lngHour;

		// NOTE: UT potentially needs to be adjusted into the range [0,24) by
		// adding/subtracting 24
		// 10. convert UT value to local time zone of latitude/longitude
		// localT = UT + localOffset
		long timezone = TimeZone.getDefault().getOffset(
				System.currentTimeMillis());
		UT = makeInRange(UT + timezone / HOUR_IN_MS, 24.0);

		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		today.setTimeInMillis(today.getTimeInMillis()
				+ (long) (UT * HOUR_IN_MS));
		return today.getTime();
	}

	private static double acos(double a) {
		return Math.toDegrees(Math.acos(a));
	}

	private static double asin(double a) {
		return Math.toDegrees(Math.asin(a));
	}

	private static double atan(double a) {
		return Math.toDegrees(Math.atan(a));
	}

	private static double cos(double a) {
		return Math.cos(Math.toRadians(a));
	}

	private static double sin(double a) {
		return Math.sin(Math.toRadians(a));
	}

	private static double tan(double a) {
		return Math.tan(Math.toRadians(a));
	}

	private static double makeInRange(double val, double max) {
		if (val >= max)
			return val - max;
		else if (val < 0)
			return val + max;
		return val;
	}

	public WeatherReport getWeather(Key key) {
		return reports.get(key);
	}
}
