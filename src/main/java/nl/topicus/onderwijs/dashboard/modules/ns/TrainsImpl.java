package nl.topicus.onderwijs.dashboard.modules.ns;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Trains;
import nl.topicus.onderwijs.dashboard.datatypes.train.Train;
import nl.topicus.onderwijs.dashboard.keys.Key;

public class TrainsImpl implements Trains {
	private NSService service;
	private Key key;

	public TrainsImpl(Key key, NSService service) {
		this.key = key;
		this.service = service;
	}

	@Override
	public List<Train> getValue() {
		return service.getTrains(key);
	}
}
