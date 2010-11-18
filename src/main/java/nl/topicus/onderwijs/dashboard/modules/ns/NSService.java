package nl.topicus.onderwijs.dashboard.modules.ns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import nl.topicus.onderwijs.dashboard.datasources.Trains;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.modules.Settings;
import nl.topicus.onderwijs.dashboard.modules.ns.model.Train;
import nl.topicus.onderwijs.dashboard.modules.ns.model.TrainType;
import nl.topicus.onderwijs.dashboard.modules.topicus.Retriever;
import nl.topicus.onderwijs.dashboard.modules.topicus.RetrieverUtils;
import nl.topicus.onderwijs.dashboard.modules.topicus.StatusPageResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NSService implements Retriever {
	private static final Logger log = LoggerFactory.getLogger(NSService.class);

	private static final int TIME = 0;
	private static final int DESTINATION = 1;
	private static final int PLATFORM = 2;
	private static final int DETAILS = 4;

	private Map<Key, List<Train>> trains = new HashMap<Key, List<Train>>();

	@Override
	public void onConfigure(Repository repository) {
		for (Key key : Settings.get().getKeysWithConfigurationFor(
				NSService.class)) {
			trains.put(key, Collections.<Train> emptyList());
			repository.addDataSource(key, Trains.class, new TrainsImpl(key,
					this));
		}
	}

	@Override
	public void refreshData() {
		Map<Key, List<Train>> newTrains = new HashMap<Key, List<Train>>();
		Map<Key, Map<String, ?>> serviceSettings = Settings.get()
				.getServiceSettings(NSService.class);
		for (Map.Entry<Key, Map<String, ?>> configEntry : serviceSettings
				.entrySet()) {
			Key location = configEntry.getKey();
			String station = configEntry.getValue().get("station").toString();
			List<Train> newDeps = fetchDepartures(location, station);
			newTrains.put(location, newDeps == null ? trains.get(location)
					: newDeps);
		}
		trains = newTrains;
	}

	private List<Train> fetchDepartures(Key location, String station) {
		try {
			StatusPageResponse response = RetrieverUtils
					.getStatuspage("http://www.ns.nl/actuele-vertrektijden/main.link?station="
							+ station);
			if (response.getHttpStatusCode() != 200) {
				return null;
			}
			Source source = new Source(response.getPageContent());

			source.fullSequentialParse();

			List<Train> newTrains = new ArrayList<Train>();
			List<Element> tableRows = source.getAllElements(HTMLElementName.TR);
			for (Element tableRow : tableRows) {
				if (tableRow.getParentElement().getName().equals(
						HTMLElementName.TBODY)) {
					Train train = new Train();
					int index = 0;
					for (Element curCell : tableRow.getChildElements()) {
						String contents = curCell.getTextExtractor().toString();
						switch (index) {
						case TIME:
							setTime(train, contents);
							break;
						case DESTINATION:
							setDestination(train, contents);
							break;
						case PLATFORM:
							setPlatform(train, contents, curCell
									.getAttributeValue("class"));
							break;
						case DETAILS:
							setDetails(train, contents);
							break;
						}
						index++;
					}
					newTrains.add(train);
				}
			}
			return newTrains;
		} catch (Exception e) {
			log.error("Unable to refresh data from ns: {} {}", e.getClass()
					.getSimpleName(), e.getMessage());
			return null;
		}
	}

	private void setTime(Train train, String content) {
		String time;
		int space = content.indexOf(' ');
		if (space > 0) {
			time = content.substring(0, space);
			try {
				String delayPart = content.substring(space + 3);
				train.setDelay(Integer.parseInt(delayPart.substring(0,
						delayPart.indexOf(' '))));
			} catch (Exception e) {
				log.info("Cannot parse delay from '" + content + "': "
						+ e.getMessage());
			}
		} else {
			time = content;
		}
		train.setDepartureTime(time);
	}

	private void setDestination(Train train, String destination) {
		train.setDestination(destination);
	}

	private void setPlatform(Train train, String platform, String classAttr) {
		train.setPlatform(platform);
		if (classAttr != null && classAttr.contains("highlight"))
			train.setPlatformChange(true);
	}

	private void setDetails(Train train, String details) {
		if (details.contains("Sprinter") || details.contains("SPR"))
			train.setType(TrainType.SPRINTER);
		else if (details.contains("Intercity"))
			train.setType(TrainType.INTERCITY);
		else if (details.contains("Sneltrein"))
			train.setType(TrainType.SNELTREIN);
		else if (details.contains("Stoptrein"))
			train.setType(TrainType.STOPTREIN);
		else if (details.contains("Int. Trein"))
			train.setType(TrainType.INTERNATIONAL);
		else if (details.contains("Fyra"))
			train.setType(TrainType.HIGH_SPEED);
		else if (details.contains("Thalys"))
			train.setType(TrainType.HIGH_SPEED);
		else if (details.contains("ICE"))
			train.setType(TrainType.HIGH_SPEED);
		else if (details.contains("Rijdt vandaag niet"))
			train.setType(TrainType.CANCELED);
		else
			train.setType(TrainType.UNKNOWN);
	}

	public List<Train> getTrains(Key key) {
		return trains.get(key);
	}
}
