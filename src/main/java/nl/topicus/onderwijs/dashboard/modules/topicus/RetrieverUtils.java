package nl.topicus.onderwijs.dashboard.modules.topicus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RetrieverUtils {
	private static final Logger log = LoggerFactory
			.getLogger(RetrieverUtils.class);

	static StatusPageResponse getStatuspage(String url) throws Exception {
		StringBuilder sb = new StringBuilder();
		HttpClient httpclient = new DefaultHttpClient();

		httpclient.getParams().setParameter("http.socket.timeout", 5000);
		httpclient.getParams().setParameter("http.connection.timeout", 5000);

		// Prepare a request object
		HttpGet httpget = new HttpGet(url);

		// Execute the request
		HttpResponse response = httpclient.execute(httpget);

		log.info("{} returns {}", url, response.getStatusLine());

		// Get hold of the response entity
		HttpEntity entity = response.getEntity();

		// If the response does not enclose an entity, there is no need
		// to worry about connection release
		if (entity != null) {
			InputStream instream = entity.getContent();
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(instream));

				String line = reader.readLine();
				while (line != null) {
					sb.append(line);
					line = reader.readLine();
				}
			} catch (IOException ex) {

				// In case of an IOException the connection will be released
				// back to the connection manager automatically
				throw ex;
			} catch (RuntimeException ex) {
				// In case of an unexpected exception you may want to abort
				// the HTTP request in order to shut down the underlying
				// connection and release it back to the connection manager.
				httpget.abort();
				throw ex;
			} finally {
				// Closing the input stream will trigger connection release
				instream.close();
			}

			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
		return new StatusPageResponse(response.getStatusLine().getStatusCode(),
				sb.toString());
	}
}
