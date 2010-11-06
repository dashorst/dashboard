package nl.topicus.onderwijs.dashboard.modules.twitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import nl.topicus.onderwijs.dashboard.modules.Repository;
import nl.topicus.onderwijs.dashboard.modules.twitter.TwitterSettings.OAuthKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.RateLimitStatus;
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
	private List<Twitter> twitters = new ArrayList<Twitter>();

	private TreeSet<Status> timeline = new TreeSet<Status>();
	private TreeSet<Status> mentions = new TreeSet<Status>();

	public TwitterService() {
	}

	public void onConfigure(Repository repository) {
		TwitterSettings settings = new TwitterSettings();

		String oAuthConsumerKey = settings.getApplicationKey().getKey();
		String oAuthConsumerSecret = settings.getApplicationKey().getSecret();

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
			Twitter twitter = new TwitterFactory().getInstance(authorization);
			twitters.add(twitter);
		}
	}

	public static void main(String[] args) {
		TwitterService twitterService = new TwitterService();
		twitterService.onConfigure(null);
		twitterService.performTwitterUpdate();
	}

	public void performTwitterUpdate() {
		for (Twitter twitter : twitters) {
			pullTweets(twitter);
		}
	}

	private void pullTweets(Twitter twitter) {
		try {
			RateLimitStatus rateLimitStatus = twitter.getRateLimitStatus();
			if (rateLimitStatus.getRemainingHits() == 0)
				return;

			double currentRate = (rateLimitStatus.getHourlyLimit() - rateLimitStatus
					.getRemainingHits())
					/ (3601 - rateLimitStatus.getSecondsUntilReset());
			logger.info(
					"Current twitter refresh rate: {}/h, official refresh rate: {}",
					String.format("%1.1f", currentRate),
					rateLimitStatus.getHourlyLimit());
			if (currentRate > rateLimitStatus.getHourlyLimit()) {
				logger.info("Skipped refreshing Twitter feeds to limit the refresh rate");
				return;
			}

			mergeStatuses(timeline, twitter.getFriendsTimeline());
			mergeStatuses(mentions, twitter.getMentions());
		} catch (TwitterException e) {
			logger.warn("Twitter reported an error: " + e.getMessage(), e);
		}
	}

	private void mergeStatuses(TreeSet<Status> originalStatuses,
			List<Status> newStatuses) {
		originalStatuses.addAll(newStatuses);

		while (originalStatuses.size() >= 40) {
			originalStatuses.pollFirst();
		}
	}
}
