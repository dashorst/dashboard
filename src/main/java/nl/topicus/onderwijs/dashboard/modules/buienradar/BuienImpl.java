package nl.topicus.onderwijs.dashboard.modules.buienradar;

import nl.topicus.onderwijs.dashboard.datasources.Buien;
import nl.topicus.onderwijs.dashboard.datatypes.BuienRadar;
import nl.topicus.onderwijs.dashboard.keys.Key;

public class BuienImpl implements Buien {
	private BuienRadarService service;
	private Key key;

	public BuienImpl(Key key, BuienRadarService service) {
		this.key = key;
		this.service = service;
	}

	@Override
	public BuienRadar getValue() {
		return service.getBuienRadar(key);
	}
}
