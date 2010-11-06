package nl.topicus.onderwijs.dashboard.modules.twitter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Settings for accessing multiple twitter accounts using OAuth.
 */
public class TwitterSettings implements Serializable {
	private static final long serialVersionUID = 1L;

	public static class OAuthKey {
		private String key;
		private String secret;

		public OAuthKey(String key, String secret) {
			this.key = key;
			this.secret = secret;
		}

		public String getKey() {
			return key;
		}

		public String getSecret() {
			return secret;
		}

		@Override
		public String toString() {
			return "key:" + key + ",secret:" + secret;
		}
	}

	private OAuthKey applicationKey;
	private HashMap<String, OAuthKey> tokens = new HashMap<String, OAuthKey>();

	public TwitterSettings() {
	}

	public OAuthKey getApplicationKey() {
		return applicationKey;
	}

	public OAuthKey getAccessKey(String account) {
		return tokens.get(account);
	}

	public void addAccessToken(String account, OAuthKey token) {
		this.tokens.put(account, token);
	}

	public Map<String, OAuthKey> getTokens() {
		return new HashMap<String, OAuthKey>(tokens);
	}
}
