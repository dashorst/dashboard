package nl.topicus.onderwijs.dashboard.modules.twitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterService {
	private static final Logger logger = LoggerFactory
			.getLogger(TwitterService.class);

	private Twitter twitter = new TwitterFactory().getInstance("topicus_board", "58L5018673");

	private List<Status> statuses = new ArrayList<Status>();

	private Timer twitterUpdateTimer;
	private int refreshesPerHour = 150;
	private RateLimitStatus mostRecentRateStatus;

	public TwitterService() {
		startPulling();
	}

	public List<Status> getStatuses() {
		return Collections.unmodifiableList(statuses);
	}

	private void startPulling() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				performTwitterUpdate();
			}
		};


		Date now = new Date();
		Date nextUpdateTry = new Date();
//		nextUpdateTry.setTime(now.getTime()
//				+ Duration.minutes(10).getMilliseconds());

		long period = Duration.ONE_HOUR.getMilliseconds() / refreshesPerHour;

		cancelTimer();
		twitterUpdateTimer = new Timer();
		twitterUpdateTimer.scheduleAtFixedRate(task, nextUpdateTry, period);
	}

	class TimelineSorter implements Comparator<Status> {
		public int compare(Status o1, Status o2) {
			return o1.getCreatedAt().compareTo(o2.getCreatedAt());
		}
	}

	private void refreshRateStatus() {
		try {
			mostRecentRateStatus = twitter.getRateLimitStatus();
			refreshesPerHour = mostRecentRateStatus.getHourlyLimit();
		} catch (TwitterException e) {
			logger.debug("Twitter reported an error: " + e.getMessage(), e);
		}
	}

	private void cancelTimer() {
		if (twitterUpdateTimer != null) {
			twitterUpdateTimer.cancel();
			twitterUpdateTimer = null;
		}
	}

	private void pullTweets() {
		try {
			List<Status> temp = twitter.getFriendsTimeline();
			Collections.sort(temp, new TimelineSorter());
			statuses = temp;
		} catch (TwitterException e) {
			logger.debug("Twitter reported an error: " + e.getMessage(), e);
		}
	}

	private void performTwitterUpdate() {
		refreshRateStatus();

		if (mostRecentRateStatus != null
				&& mostRecentRateStatus.getRemainingHits() > 0)
			pullTweets();
	}
}
