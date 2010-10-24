package nl.topicus.onderwijs.dashboard.modules;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A project is an application of which certain statistics can be gathererd,
 * such as build status, application status, commit messages, number of
 * sessions. Multiple builds can be attached to a project, as well as multiple
 * servers.
 */
public class Project extends AbstractCodeNameKey {
	private static final long serialVersionUID = 1L;

	public Project(String code, String name) {
		super(code, name);
	}

	@JsonCreator
	public static Project from(@JsonProperty("code") String code,
			@JsonProperty("name") String name) {
		return new Project(code, name);
	}
}
