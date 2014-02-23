package stock.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

public class DownloadHelper {

	private static final Logger logger = Logger.getLogger(DownloadHelper.class);
	private final HttpClientBuilder clientBuilder;
	
	public DownloadHelper(final HttpClientBuilder clientBuilder) {
		this.clientBuilder = clientBuilder;
	}
	
	public HttpResponse download(final String url,final OutputStream outputStream) {
		HttpResponse response = null;
		try {
			final HttpClient client = clientBuilder.build();
			final HttpUriRequest get = new HttpGet(url);
			final WritingResponseHandler responseHandler = 
					new WritingResponseHandler(url, outputStream);
			try {
				response = client.execute(get, responseHandler);
			} catch (ClientProtocolException e) {
				logger.error(e.getMessage(), e);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
		return response;
	}
	
	private static final class WritingResponseHandler implements ResponseHandler<HttpResponse> {
		private final OutputStream outputStream;
		private final String url;
		private boolean passed = false;
		
		public WritingResponseHandler(final String url, final OutputStream outputStream) {
			this.outputStream = outputStream;
			this.url = url;
		}
		
		@Override
		public HttpResponse handleResponse(final HttpResponse response)
				throws ClientProtocolException, IOException {
			int sc = response.getStatusLine().getStatusCode();
			passed = sc >= 200 && sc < 300;
			if (passed) {
					final InputStream in = response.getEntity().getContent();
					try {
						IOUtils.copy(in, outputStream);
					} finally {
						IOUtils.closeQuietly(in);
					}
			} else {
				logger.warn("Failed to download from " + url + ", got "
						+ sc);
			}
			return response;
		}
		
		public boolean isPassed() {
			return passed;
		}
	}
}