package nl.topicus.onderwijs.dashboard.modules.topicus;

import nl.topicus.onderwijs.dashboard.keys.Key;

interface TopicusApplicationStatusProvider {
	TopicusApplicationStatus getStatus(Key project);
}
