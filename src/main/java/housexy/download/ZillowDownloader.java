package housexy.download;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

public class ZillowDownloader {
	private static final Logger logger = Logger
			.getLogger(ZillowDownloader.class);

	private final ZillowLinkSource source;
	private final SearchProcessor searchProcessor;

	public ZillowDownloader(ZillowLinkSource source,
			SearchProcessor searchProcessor) {
		this.source = source;
		this.searchProcessor = searchProcessor;
	}

	public void download() {
		HttpClient client = HttpClientBuilder.create().build();
		for (String url : source.getLinks()) {
			try {
				HttpGet get = new HttpGet(url);
				HttpResponse response = client.execute(get);
				searchProcessor.process(response.getEntity().getContent());
			} catch (ClientProtocolException e) {
				logger.error("Failed to get from url: " + url, e);
			} catch (IOException e) {
				logger.error("Failed to get from url: " + url, e);
			}
		}
	}
}
