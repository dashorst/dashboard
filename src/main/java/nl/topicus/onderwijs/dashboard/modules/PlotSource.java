package nl.topicus.onderwijs.dashboard.modules;

import nl.topicus.wqplot.components.JQPlot;

public interface PlotSource {
	public JQPlot createPlot(String id);
}
