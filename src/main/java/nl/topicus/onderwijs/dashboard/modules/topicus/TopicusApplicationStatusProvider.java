package nl.topicus.onderwijs.dashboard.modules.topicus;

import nl.topicus.onderwijs.dashboard.modules.Project;

interface TopicusApplicationStatusProvider {
	TopicusApplicationStatus getStatus(Project project);
}
