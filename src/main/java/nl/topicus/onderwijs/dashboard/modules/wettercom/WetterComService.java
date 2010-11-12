package nl.topicus.onderwijs.dashboard.modules.wettercom;

import java.io.StringReader;
import java.security.MessageDigest;
import java.util.Map;
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

	public static void main(String[] args) {
		new WetterComService().refreshData();
	}

	public WeatherReport getWeather(Key key) {
		return reports.get(key);
	}
}
