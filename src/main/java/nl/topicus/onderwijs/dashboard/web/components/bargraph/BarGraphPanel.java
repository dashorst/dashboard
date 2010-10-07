package nl.topicus.onderwijs.dashboard.web.components.bargraph;

import java.util.Arrays;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public class BarGraphPanel extends Panel {
	private static final long serialVersionUID = 1L;

	public BarGraphPanel(String id) {
		super(id);
		ListView<String> bars = new ListView<String>("bars", Arrays.asList(
				"EduArte", "@VO", "PassePartout", "DUO", "Test")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<String> item) {
				item.add(new BarGraphBarPanel("bar", item.getModel()));
			}
		};
		add(bars);
	}
}
