package nl.topicus.onderwijs.dashboard.modules.ns;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Trains;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.ns.model.Train;

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
