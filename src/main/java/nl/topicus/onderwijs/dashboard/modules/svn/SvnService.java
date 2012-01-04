package nl.topicus.onderwijs.dashboard.modules.svn;

import java.util.ArrayList;
import java.util.Collections;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

@Service
@ServiceConfiguration(interval = 1, unit = TimeUnit.MINUTES)
public class SvnService extends AbstractService {
	private static final Logger log = LoggerFactory.getLogger(SvnService.class);

	private Map<Key, List<Commit>> commits = new HashMap<Key, List<Commit>>();

	@Autowired
	public SvnService(ISettings settings) {
		super(settings);
	}

	@Override
	public void onConfigure(DashboardRepository repository) {
		DAVRepositoryFactory.setup();
		FSRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		for (Key key : getSettings().getKeysWithConfigurationFor(
				SvnService.class)) {
			commits.put(key, Collections.<Commit> emptyList());
			repository.addDataSource(key, Commits.class, new CommitsImpl(key,
					this));
		}
	}

	@Override
	public void refreshData() {
		Map<Key, List<Commit>> newCommits = new HashMap<Key, List<Commit>>();
		Map<Key, Map<String, ?>> serviceSettings = getSettings()
				.getServiceSettings(SvnService.class);
		for (Map.Entry<Key, Map<String, ?>> configEntry : serviceSettings
				.entrySet()) {
			Key project = configEntry.getKey();
			String url = configEntry.getValue().get("url").toString();
			String username = configEntry.getValue().get("username").toString();
			String password = configEntry.getValue().get("password").toString();
			newCommits.put(project,
					fetchCommits(project, url, username, password));
		}
		commits = newCommits;
	}

	private List<Commit> fetchCommits(final Key project, String url,
			String username, String password) {
		try {
			final List<Commit> ret = new ArrayList<Commit>();
			SVNURL svnUrl = SVNURL.parseURIDecoded(url);
			SVNClientManager clientManager = SVNClientManager.newInstance(
					new DefaultSVNOptions(), username, password);
			SVNLogClient logClient = clientManager.getLogClient();
			logClient.doLog(svnUrl, new String[] { "." }, SVNRevision.HEAD,
					SVNRevision.HEAD, SVNRevision.create(0), true, true, 10,
					new ISVNLogEntryHandler() {
						@Override
						public void handleLogEntry(SVNLogEntry logEntry)
								throws SVNException {
							ret.add(new Commit(project, logEntry));
						}
					});
			clientManager.dispose();
			return ret;
		} catch (SVNException e) {
			log.error("Unable to refresh data from svn: {} {}", e.getClass()
					.getSimpleName(), e.getMessage());
		}
		return Collections.emptyList();
	}

	public List<Commit> getCommits(Key key) {
		return commits.get(key);
	}
}