package jinvestor.jhouse.download;

/*
 * #%L
 * jinvestor.parent
 * %%
 * Copyright (C) 2014 Michael Scott Knapp
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
		logger.debug("Starting to download home data from zillow.");
		for (String url : source.getLinks()) {
			try {
				logger.info(String.format("Downloading from %s",url));
				HttpGet get = new HttpGet(url);
				HttpResponse response = client.execute(get);
				searchProcessor.process(response.getEntity().getContent());
			} catch (ClientProtocolException e) {
				logger.error("Failed to get from url: " + url, e);
			} catch (IOException e) {
				logger.error("Failed to get from url: " + url, e);
			}
		}
		logger.debug("Done downloading home data from zillow.");
	}
}
