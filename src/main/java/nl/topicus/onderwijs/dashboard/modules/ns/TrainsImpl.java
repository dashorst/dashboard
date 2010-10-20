package nl.topicus.onderwijs.dashboard.modules.ns;

import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.Trains;
import nl.topicus.onderwijs.dashboard.modules.ns.model.Train;

public class TrainsImpl implements Trains {
	private NSService service;

	public TrainsImpl(NSService service) {
		this.service = service;
	}

	@Override
	public List<Train> getValue() {
		return service.getTrains();
	}
}
