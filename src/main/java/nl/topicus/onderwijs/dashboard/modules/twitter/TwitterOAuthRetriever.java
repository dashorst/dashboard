package nl.topicus.onderwijs.dashboard.modules.twitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class TwitterOAuthRetriever {
	public static void main(String args[]) throws Exception {

		TwitterSettings settings = new TwitterSettings();

		String oAuthConsumerKey = settings.getApplicationKey().getKey();
		String oAuthConsumerSecret = settings.getApplicationKey().getSecret();

		// The factory instance is re-useable and thread safe.
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(oAuthConsumerKey, oAuthConsumerSecret);
		RequestToken requestToken = twitter.getOAuthRequestToken("oob");
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			System.out
					.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			System.out
					.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
			String pin = br.readLine();
			try {
				if (pin.length() > 0) {
					accessToken = twitter
							.getOAuthAccessToken(requestToken, pin);
				} else {
					accessToken = twitter.getOAuthAccessToken();
				}
			} catch (TwitterException te) {
				if (401 == te.getStatusCode()) {
					System.out.println("Unable to get the access token.");
				} else {
					te.printStackTrace();
				}
			}
		}
		// persist to the accessToken for future reference.
		storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
		Status status = twitter.updateStatus(args[0]);
		System.out.println("Successfully updated the status to ["
				+ status.getText() + "].");
		System.exit(0);
	}

	private static void storeAccessToken(int useId, AccessToken accessToken) {
		System.out.printf("Use id %d, token: \"%s\", secret: \"%s\"\n", useId,
				accessToken.getToken(), accessToken.getTokenSecret());
	}
}
