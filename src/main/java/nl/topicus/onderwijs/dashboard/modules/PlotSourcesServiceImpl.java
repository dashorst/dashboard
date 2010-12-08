package nl.topicus.onderwijs.dashboard.modules;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlotSourcesServiceImpl implements PlotSourcesService {
	private List<PlotSource> plotSources;

	@Autowired
	public PlotSourcesServiceImpl(List<PlotSource> plotSources) {
		this.plotSources = plotSources;
	}

	@Override
	public List<PlotSource> getPlotSources() {
		return plotSources;
	}
}
