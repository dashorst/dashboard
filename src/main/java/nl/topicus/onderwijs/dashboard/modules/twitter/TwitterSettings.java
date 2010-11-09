package nl.topicus.onderwijs.dashboard.modules.twitter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Settings for accessing multiple twitter accounts using OAuth.
 */
public class TwitterSettings implements Serializable {
	private static final long serialVersionUID = 1L;

	public static class OAuthKey {
		private String key;
		private String secret;

		public OAuthKey(Map<String, String> keyAndSecret) {
			key = keyAndSecret.get("key");
			secret = keyAndSecret.get("secret");
		}

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

	@SuppressWarnings("unchecked")
	public TwitterSettings(Map<String, ?> settings) {
		applicationKey = new OAuthKey((Map<String, String>) settings
				.get("application"));
		for (Entry<String, ?> curEntry : settings.entrySet()) {
			if (!"application".equals(curEntry.getKey())) {
				tokens.put(curEntry.getKey(), new OAuthKey(
						(Map<String, String>) curEntry.getValue()));
			}
		}
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
