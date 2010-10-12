package nl.topicus.onderwijs.dashboard.modules.hudson;

import nl.topicus.onderwijs.dashboard.modules.hudson.model.Project;
import nl.topicus.onderwijs.dashboard.modules.topicus.RetrieverUtils;
import nl.topicus.onderwijs.dashboard.modules.topicus.StatusPageResponse;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;

public class HudsonService {
	private final WicketApplication application;

	public HudsonService(WicketApplication application) {
		this.application = application;
	}

	public static void main(String[] args) {
		try {
			StatusPageResponse response = RetrieverUtils
					.getStatuspage("http://builds.topicus.local/job/DUO/api/json");
			// .getStatuspage("http://localhost:8080/duo.json");
			System.out.println(response.getHttpStatusCode());
			System.out.println(response.getPageContent());
			ObjectMapper mapper = new ObjectMapper();
			mapper.getDeserializationConfig().disable(
					Feature.FAIL_ON_UNKNOWN_PROPERTIES);
			Project project = mapper.readValue(response.getPageContent(),
					Project.class);
			mapper.writeValue(System.out, project);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void foo() {

	}
}
