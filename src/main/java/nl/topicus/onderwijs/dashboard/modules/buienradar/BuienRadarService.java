package nl.topicus.onderwijs.dashboard.modules.buienradar;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import nl.topicus.onderwijs.dashboard.config.ISettings;
import nl.topicus.onderwijs.dashboard.datasources.Buien;
import nl.topicus.onderwijs.dashboard.datatypes.BuienRadar;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.AbstractService;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.modules.ServiceConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ServiceConfiguration(interval = 5, unit = TimeUnit.MINUTES)
public class BuienRadarService extends AbstractService {
	private static final Logger log = LoggerFactory
			.getLogger(BuienRadarService.class);

	private Map<Key, BuienRadar> reports = new ConcurrentHashMap<Key, BuienRadar>();

	@Autowired
	public BuienRadarService(ISettings settings) {
		super(settings);
	}

	@Override
	public void onConfigure(DashboardRepository repository) {
		for (Key key : getSettings().getKeysWithConfigurationFor(
				BuienRadarService.class)) {
			repository
					.addDataSource(key, Buien.class, new BuienImpl(key, this));
		}
	}

	@Override
	public void refreshData() {
		try {
			Map<Key, Map<String, ?>> serviceSettings = getSettings()
					.getServiceSettings(BuienRadarService.class);
			for (Map.Entry<Key, Map<String, ?>> curSettingEntry : serviceSettings
					.entrySet()) {
				BuienRadar report = new BuienRadar();

				Map<String, ?> locatieSettings = curSettingEntry.getValue();
				int lat = (Integer) locatieSettings.get("lat");
				int lon = (Integer) locatieSettings.get("lon");
				int x = (Integer) locatieSettings.get("x");
				int y = (Integer) locatieSettings.get("y");

				List<Integer> kleurenBuienradar = getKleurenBuienradar(lat,
						lon, x, y);
				Integer[] rainForecast = new Integer[kleurenBuienradar.size()];
				for (int i = 0; i < rainForecast.length; i++)
					rainForecast[i] = kleurenBuienradar.get(i);
				report.setRainForecast(rainForecast);

				reports.put(curSettingEntry.getKey(), report);
			}
		} catch (Exception e) {
			log.error("Unable to refresh data from buienradar.nl: {} {}", e
					.getClass().getSimpleName(), e);
		}
	}

	public static List<Integer> getKleurenBuienradar(int lat, int lon, int x,
			int y) {
		List<Integer> colors = new ArrayList<Integer>();
		try {
			for (int id = 0; id < 12; id++) {
				URL url = new URL(
						"http://www1.buienradar.nl/zoomnlimage.php?lat=" + lat
								+ "&lon=" + lon + "&id=" + (id));
				ImageReader reader = ImageIO.getImageReadersBySuffix("gif")
						.next();
				reader.setInput(ImageIO.createImageInputStream(url.openStream()));
				BufferedImage image = reader.read(0);
				Color c = new Color(image.getRGB(x, y), true);
				colors.add(3 * c.getAlpha());
			}
			log.debug("Buienradar: " + colors);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return colors;
	}

	public BuienRadar getBuienRadar(Key key) {
		return reports.get(key);
	}
}
