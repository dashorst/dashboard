package nl.topicus.onderwijs.dashboard.timers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.modules.topicus.ParnassysStatusRetriever;
import nl.topicus.onderwijs.dashboard.modules.topicus.TopicusApplicationStatus;
import nl.topicus.onderwijs.dashboard.modules.topicus.VocusOuderportaalRetriever;
import nl.topicus.onderwijs.dashboard.modules.topicus.VocusStatusRetriever;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicusProjectsUpdateTask extends TimerTask {
	private static final Logger log = LoggerFactory
			.getLogger(TopicusProjectsUpdateTask.class);
	private List<Project> projects;
	private WicketApplication application;

	public TopicusProjectsUpdateTask(WicketApplication application,
			List<Project> projects) {
		this.projects = projects;
		this.application = application;
	}

	@Override
	public void run() {
		log.info("Topicus Project updates start");
		Map<Project, TopicusApplicationStatus> statusses = new HashMap<Project, TopicusApplicationStatus>();
		for (Project project : projects) {
			TopicusApplicationStatus status = null;
			if ("atvo".equals(project.getCode())) {
				status = getVocusStatus(project);
			} else if ("atvo_ouders".equals(project.getCode())) {
				status = getOuderportalStatus(project);
			} else if ("parnassys".equals(project.getCode())) {
				status = getParnassysStatus(project);
			}
			if (status != null) {
				statusses.put(project, status);
			}
		}
		application.updateStatusses(statusses);
		log.info("Topicus Project updates complete");
	}

	private TopicusApplicationStatus getVocusStatus(Project project) {
		VocusStatusRetriever vocusRetriever = new VocusStatusRetriever();
		return vocusRetriever.getProjectData(project);
	}

	private TopicusApplicationStatus getOuderportalStatus(Project project) {
		VocusOuderportaalRetriever retriever = new VocusOuderportaalRetriever();
		return retriever.getProjectData(project);
	}

	private TopicusApplicationStatus getParnassysStatus(Project project) {
		ParnassysStatusRetriever retriever = new ParnassysStatusRetriever();
		return retriever.getProjectData(project);
	}
}
