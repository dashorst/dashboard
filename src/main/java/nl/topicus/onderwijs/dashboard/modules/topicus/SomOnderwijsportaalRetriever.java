package nl.topicus.onderwijs.dashboard.modules.topicus;

import static nl.topicus.onderwijs.dashboard.modules.topicus.RetrieverUtils.getStatuspage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import nl.topicus.onderwijs.dashboard.config.ISettings;
import nl.topicus.onderwijs.dashboard.datasources.ApplicationVersion;
import nl.topicus.onderwijs.dashboard.datasources.AverageRequestTime;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfServers;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfServersOffline;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsersPerServer;
import nl.topicus.onderwijs.dashboard.datasources.RequestsPerMinute;
import nl.topicus.onderwijs.dashboard.datasources.ServerAlerts;
import nl.topicus.onderwijs.dashboard.datasources.ServerStatus;
import nl.topicus.onderwijs.dashboard.datasources.Uptime;
import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.datatypes.DotColor;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.AbstractService;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.modules.ServiceConfiguration;

import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ServiceConfiguration(interval = 30, unit = TimeUnit.SECONDS)
public class SomOnderwijsportaalRetriever extends AbstractService implements
		TopicusApplicationStatusProvider {
	private static final Logger log = LoggerFactory
			.getLogger(SomOnderwijsportaalRetriever.class);

	private Map<Key, TopicusApplicationStatus> statusses = new HashMap<Key, TopicusApplicationStatus>();

	private Map<String, Alert> oldAlerts = new HashMap<String, Alert>();

	@Autowired
	public SomOnderwijsportaalRetriever(ISettings settings) {
		super(settings);
	}

	@Override
	public void onConfigure(DashboardRepository repository) {
		Map<Key, Map<String, ?>> serviceSettings = getSettings()
				.getServiceSettings(SomOnderwijsportaalRetriever.class);
		for (Key project : serviceSettings.keySet()) {
			repository.addDataSource(project, NumberOfUsers.class,
					new NumberOfUsersImpl(project, this));
			repository.addDataSource(project, NumberOfServers.class,
					new NumberOfServersImpl(project, this));
			repository.addDataSource(project, NumberOfServersOffline.class,
					new NumberOfServersOfflineImpl(project, this));
			repository.addDataSource(project, Uptime.class, new UptimeImpl(
					project, this));
			repository.addDataSource(project, ApplicationVersion.class,
					new ApplicationVersionImpl(project, this));
			repository.addDataSource(project, ServerStatus.class,
					new ServerStatusImpl(project, this));
			repository.addDataSource(project, ServerAlerts.class,
					new AlertsImpl(project, this));
			repository.addDataSource(project, NumberOfUsersPerServer.class,
					new NumberOfUsersPerServerImpl(project, this));
			repository.addDataSource(project, AverageRequestTime.class,
					new AverageRequestTimeImpl(project, this));
			repository.addDataSource(project, RequestsPerMinute.class,
					new RequestsPerMinuteImpl(project, this));
		}
	}

	@Override
	public TopicusApplicationStatus getStatus(Key project) {
		return statusses.get(project);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refreshData() {
		HashMap<Key, TopicusApplicationStatus> newStatusses = new HashMap<Key, TopicusApplicationStatus>();
		Map<Key, Map<String, ?>> serviceSettings = getSettings()
				.getServiceSettings(SomOnderwijsportaalRetriever.class);

		for (Map.Entry<Key, Map<String, ?>> configEntry : serviceSettings
				.entrySet()) {
			Key project = configEntry.getKey();
			Map<String, String> urls = (Map<String, String>) configEntry
					.getValue().get("urls");
			TopicusApplicationStatus status = getProjectData(project, urls);
			newStatusses.put(project, status);
		}
		statusses = newStatusses;
	}

	private TopicusApplicationStatus getProjectData(Key project,
			Map<String, String> urls) {
		TopicusApplicationStatus status = new TopicusApplicationStatus();
		if (urls == null || urls.isEmpty()) {
			return status;
		}
		List<Alert> alerts = new ArrayList<Alert>();
		for (Entry<String, String> statusUrlEntry : urls.entrySet()) {
			String statusCode = statusUrlEntry.getKey();
			String statusUrl = statusUrlEntry.getValue();
			TopicusServerStatus server = new TopicusServerStatus(statusCode,
					statusUrl);
			status.addServer(server);
			Alert oldAlert = oldAlerts.get(statusUrl);
			try {
				StatusPageResponse statuspage = getStatuspage(statusUrl);
				if (!statuspage.isOk()) {
					server.setServerStatus(DotColor.RED);
					Alert alert = new Alert(oldAlert, DotColor.RED, project,
							"Server " + statusCode + " offline with HTTP code "
									+ statuspage.getHttpStatusCode());
					oldAlerts.put(statusUrl, alert);
					alerts.add(alert);
					continue;
				}
				server.setServerStatus(DotColor.GREEN);
				String page = statuspage.getPageContent();

				Source source = new Source(page);

				source.fullSequentialParse();

				List<Element> tableHeaders = source
						.getAllElements(HTMLElementName.H2);
				for (Element tableHeader : tableHeaders) {
					String contents = tableHeader.getTextExtractor().toString();
					if ("Applicatie status".equals(contents)) {
						fetchApplicationInfo(server, tableHeader
								.getParentElement(), oldAlert, alerts, project);
					} else if ("Sessies/Requests".equals(contents)) {
						fetchSessionAndRequestData(server, tableHeader
								.getParentElement());
					}
				}
				oldAlerts.put(statusUrl, null);
			} catch (Exception e) {
				server.setServerStatus(DotColor.YELLOW);
				Alert alert = new Alert(oldAlert, DotColor.YELLOW, project, e
						.getMessage());
				oldAlerts.put(statusUrl, alert);
				alerts.add(alert);
				log.warn("Could not retrieve status for '" + statusUrl + "': "
						+ e.getClass().getSimpleName() + " - "
						+ e.getLocalizedMessage());
			}
		}
		status.setAlerts(alerts);
		log.info("Application status: {}->{}", project, status);
		return status;
	}

	private void fetchApplicationInfo(TopicusServerStatus server,
			Element tableHeader, Alert oldAlert, List<Alert> alerts, Key project) {
		List<Element> tableRows = tableHeader
				.getAllElements(HTMLElementName.TR);
		for (Element curRow : tableRows) {
			String name = curRow.getFirstElement("th").getTextExtractor()
					.toString();
			String value = curRow.getFirstElement("td").getTextExtractor()
					.toString();
			if ("Versie".equals(name)) {
				server.setVersion(value);
			} else if ("Start tijd".equals(name)) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"dd MMMM yyyy, hh:mm", new Locale("NL"));
				try {
					Date starttime = sdf.parse(value);
					Date now = new Date();
					server.setUptime(Duration.milliseconds(
							now.getTime() - starttime.getTime())
							.getMilliseconds());
				} catch (ParseException e) {
					log.error("Unable to parse starttime " + value
							+ " according to format dd MMMM yyyy, hh:mm", e);
				}
			} else if ("Status".equals(name)) {
				if (!"OK".equals(value)) {
					server.setServerStatus(DotColor.RED);
					Alert alert = new Alert(oldAlert, DotColor.RED, project,
							"Server " + server.getCode() + " reports " + value);
					oldAlerts.put(server.getUrl(), alert);
					alerts.add(alert);
				}
			}
		}
	}

	private void fetchSessionAndRequestData(TopicusServerStatus server,
			Element tableHeader) {
		List<Element> tableRows = tableHeader
				.getAllElements(HTMLElementName.TR);
		for (Element curRow : tableRows) {
			String name = curRow.getFirstElement("th").getTextExtractor()
					.toString();
			String value = curRow.getFirstElement("td").getTextExtractor()
					.toString();
			if ("Actieve sessies".equals(name)) {
				try {
					server.setNumberOfUsers(Integer.parseInt(value));
				} catch (NumberFormatException e) {
					log.error("Cannot parse number of users: " + value);
				}
			} else if ("Gem. request duur".equals(name)) {
				try {
					int space = value.indexOf(' ');
					if (space > -1) {
						value = value.substring(0, space);
					}
					server.setAverageRequestDuration(Integer.parseInt(value));
				} catch (NumberFormatException e) {
					log.error("Cannot parse avg request duration: " + value);
				}
			} else if ("Requests per minuut".equals(name)) {
				try {
					server.setRequestsPerMinute(Integer.parseInt(value));
				} catch (NumberFormatException e) {
					log.error("Cannot parse req per minute: " + value);
				}
			}
		}
	}
}
