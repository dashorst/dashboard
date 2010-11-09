package nl.topicus.onderwijs.dashboard.modules.twitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.modules.Settings;
import nl.topicus.onderwijs.dashboard.modules.twitter.TwitterSettings.OAuthKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.http.Authorization;
import twitter4j.http.AuthorizationFactory;

public class TwitterService {
	private static final Logger logger = LoggerFactory
			.getLogger(TwitterService.class);
	private Map<Key, List<Twitter>> twitters = new HashMap<Key, List<Twitter>>();

	private Map<Key, NavigableSet<Status>> timeline = new HashMap<Key, NavigableSet<Status>>();
	private Map<Key, NavigableSet<Status>> mentions = new HashMap<Key, NavigableSet<Status>>();

	public TwitterService() {
	}

	public void onConfigure(Repository repository) {
		for (Entry<Key, Map<String, ?>> settingsEntry : Settings.get()
				.getServiceSettings(TwitterService.class).entrySet()) {
			Key key = settingsEntry.getKey();

			TwitterSettings settings = new TwitterSettings(settingsEntry
					.getValue());

			String oAuthConsumerKey = settings.getApplicationKey().getKey();
			String oAuthConsumerSecret = settings.getApplicationKey()
					.getSecret();

			List<Twitter> keyTwitters = new ArrayList<Twitter>();
			for (Entry<String, OAuthKey> tokenEntry : settings.getTokens()
					.entrySet()) {

				OAuthKey token = tokenEntry.getValue();

				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(oAuthConsumerKey);
				builder.setOAuthConsumerSecret(oAuthConsumerSecret);
				builder.setOAuthAccessToken(token.getKey());
				builder.setOAuthAccessTokenSecret(token.getSecret());
				Configuration conf = builder.build();

				Authorization authorization = AuthorizationFactory.getInstance(
						conf, true);
				Twitter twitter = new TwitterFactory()
						.getInstance(authorization);
				keyTwitters.add(twitter);
			}
			twitters.put(key, keyTwitters);
			mentions.put(key, new TreeSet<Status>());
			timeline.put(key, new TreeSet<Status>());
		}
	}

	public static void main(String[] args) {
		TwitterService twitterService = new TwitterService();
		twitterService.onConfigure(null);
		twitterService.performTwitterUpdate();
	}

	public void performTwitterUpdate() {
		for (Entry<Key, List<Twitter>> twitterEntry : twitters.entrySet()) {
			for (Twitter twitter : twitterEntry.getValue()) {
				pullTweets(twitterEntry.getKey(), twitter);
			}
		}
	}

	private void pullTweets(Key key, Twitter twitter) {
		try {
			RateLimitStatus rateLimitStatus = twitter.getRateLimitStatus();
			if (rateLimitStatus.getRemainingHits() == 0)
				return;

			double currentRate = (rateLimitStatus.getHourlyLimit() - rateLimitStatus
					.getRemainingHits())
					/ (3601 - rateLimitStatus.getSecondsUntilReset());
			logger
					.info(
							"Current twitter refresh rate: {}/h, official refresh rate: {}",
							String.format("%1.1f", currentRate),
							rateLimitStatus.getHourlyLimit());
			if (currentRate > rateLimitStatus.getHourlyLimit()) {
				logger
						.info("Skipped refreshing Twitter feeds to limit the refresh rate");
				return;
			}

			ResponseList<Status> newFriendsTimeline = twitter
					.getFriendsTimeline();
			ResponseList<Status> newMentions = twitter.getMentions();
			synchronized (this) {
				mergeStatuses(timeline.get(key), newFriendsTimeline);
				mergeStatuses(mentions.get(key), newMentions);
			}
		} catch (TwitterException e) {
			logger.warn("Twitter reported an error: " + e.getMessage(), e);
		}
	}

	private void mergeStatuses(NavigableSet<Status> originalStatuses,
			List<Status> newStatuses) {
		originalStatuses.addAll(newStatuses);

		while (originalStatuses.size() >= 40) {
			originalStatuses.pollFirst();
		}
	}

	public synchronized List<Status> getTimeline(Key key) {
		return new ArrayList<Status>(timeline.get(key));
	}

	public synchronized List<Status> getMentions(Key key) {
		return new ArrayList<Status>(mentions.get(key));
	}
}
