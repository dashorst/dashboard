package nl.topicus.onderwijs.dashboard.modules.hudson;

import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.Build;
import nl.topicus.onderwijs.dashboard.modules.hudson.model.BuildReference;

public interface BuildProvider {
	Build getBuild(Project project, BuildReference reference);
}
