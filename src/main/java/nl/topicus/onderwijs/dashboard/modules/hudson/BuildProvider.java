package nl.topicus.onderwijs.dashboard.modules.hudson;

import nl.topicus.onderwijs.dashboard.datatypes.hudson.Build;
import nl.topicus.onderwijs.dashboard.datatypes.hudson.BuildReference;
import nl.topicus.onderwijs.dashboard.keys.Project;

public interface BuildProvider {
	Build getBuild(Project project, BuildReference reference);
}
