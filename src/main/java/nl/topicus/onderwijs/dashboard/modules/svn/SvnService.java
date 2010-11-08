package nl.topicus.onderwijs.dashboard.modules.svn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.topicus.onderwijs.dashboard.datasources.Commits;
import nl.topicus.onderwijs.dashboard.datatypes.Commit;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.modules.Settings;
import nl.topicus.onderwijs.dashboard.modules.mantis.MantisService;
import nl.topicus.onderwijs.dashboard.modules.ns.NSService;
import nl.topicus.onderwijs.dashboard.modules.topicus.Retriever;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

public class SvnService implements Retriever {
	private static final Logger log = LoggerFactory.getLogger(NSService.class);

	private Map<Key, List<Commit>> commits = new HashMap<Key, List<Commit>>();

	@Override
	public void onConfigure(Repository repository) {
		DAVRepositoryFactory.setup();
		for (Key key : Settings.get().getKeysWithConfigurationFor(
				MantisService.class)) {
			commits.put(key, Collections.<Commit> emptyList());
			repository.addDataSource(key, Commits.class, new CommitsImpl(key,
					this));
		}
	}

	@Override
	public void refreshData() {
		Map<Key, List<Commit>> newCommits = new HashMap<Key, List<Commit>>();
		Map<Key, Map<String, ?>> serviceSettings = Settings.get()
				.getServiceSettings(SvnService.class);
		for (Map.Entry<Key, Map<String, ?>> configEntry : serviceSettings
				.entrySet()) {
			Key project = configEntry.getKey();
			String url = configEntry.getValue().get("url").toString();
			String username = configEntry.getValue().get("username").toString();
			String password = configEntry.getValue().get("password").toString();
			newCommits.put(project, fetchCommits(project, url, username,
					password));
		}
		System.out.println(newCommits);
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