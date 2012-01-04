package nl.topicus.onderwijs.dashboard.modules.github;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import nl.topicus.onderwijs.dashboard.config.ISettings;
import nl.topicus.onderwijs.dashboard.datasources.Commits;
import nl.topicus.onderwijs.dashboard.datatypes.Commit;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.AbstractService;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.modules.ServiceConfiguration;

import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GsonUtils;
import org.eclipse.egit.github.core.service.CommitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;

@Service
@ServiceConfiguration(interval = 1, unit = TimeUnit.MINUTES)
public class GitHubService extends AbstractService {
	private static final Logger log = LoggerFactory
			.getLogger(GitHubService.class);

	private Map<String, RepositoryCommit> fullCommits = new HashMap<String, RepositoryCommit>();

	private Map<Key, List<Commit>> commits = new HashMap<Key, List<Commit>>();

	@Autowired
	public GitHubService(ISettings settings) {
		super(settings);
	}

	@Override
	public void onConfigure(DashboardRepository repository) {
		for (Key key : getSettings().getKeysWithConfigurationFor(
				GitHubService.class)) {
			commits.put(key, Collections.<Commit> emptyList());
			repository.addDataSource(key, Commits.class, new CommitsImpl(key,
					this));
		}
		try {
			Field deserializersField = Gson.class
					.getDeclaredField("deserializers");
			deserializersField.setAccessible(true);
			Object deserializers = deserializersField.get(GsonUtils.getGson());
			Field deserializersMapField = deserializers.getClass()
					.getDeclaredField("map");
			deserializersMapField.setAccessible(true);
			Map<Type, JsonDeserializer<?>> deserializersMap = (Map<Type, JsonDeserializer<?>>) deserializersMapField
					.get(deserializers);
			deserializersMap.put(Date.class, new GitHubDateFormatter());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void refreshData() {
		Map<Key, List<Commit>> newCommits = new HashMap<Key, List<Commit>>();
		Map<Key, Map<String, ?>> serviceSettings = getSettings()
				.getServiceSettings(GitHubService.class);
		for (Map.Entry<Key, Map<String, ?>> configEntry : serviceSettings
				.entrySet()) {
			try {
				Key project = configEntry.getKey();
				String owner = configEntry.getValue().get("owner").toString();
				String repository = configEntry.getValue().get("repository")
						.toString();
				String username = (String) configEntry.getValue().get(
						"username");
				String password = (String) configEntry.getValue().get(
						"password");
				String token = (String) configEntry.getValue().get("token");
				newCommits.put(
						project,
						fetchCommits(project, owner, repository, username,
								password, token));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		commits = newCommits;
	}

	private List<Commit> fetchCommits(final Key project, String owner,
			String repository, String username, String password, String token)
			throws IOException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		Date oneDayAgo = cal.getTime();

		CommitService commits = new CommitService();
		if (token != null)
			commits.getClient().setOAuth2Token(token);
		else
			commits.getClient().setCredentials(username, password);
		final List<Commit> ret = new ArrayList<Commit>();
		RepositoryId repositoryId = new RepositoryId(owner, repository);
		for (RepositoryCommit curCommit : commits.pageCommits(repositoryId, 30)
				.next()) {
			if (curCommit.getCommit().getCommitter().getDate().after(oneDayAgo)) {
				RepositoryCommit fullCommit = fullCommits.get(curCommit
						.getSha());
				if (fullCommit == null) {
					fullCommit = commits.getCommit(repositoryId,
							curCommit.getSha());
					fullCommits.put(curCommit.getSha(), fullCommit);
				}
				curCommit = fullCommit;
			}
			ret.add(new Commit(project, curCommit));
		}
		return ret;
	}

	public List<Commit> getCommits(Key key) {
		return commits.get(key);
	}
}