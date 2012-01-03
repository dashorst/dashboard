package nl.topicus.onderwijs.dashboard.modules.hudson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.map.util.StdDateFormat;

public class HudsonDateFormat extends StdDateFormat {
	private static final long serialVersionUID = 1L;

	public HudsonDateFormat() {
	}

	@Override
	public StdDateFormat clone() {
		return new HudsonDateFormat();
	}

	@Override
	public Date parse(String dateStr) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		try {
			return format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return super.parse(dateStr);
	}
}
