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

import java.util.ArrayList;
import java.util.List;

import jinvestor.jhouse.core.ZillowSearchUrl;

public class SimpleZillowLinkSource implements ZillowLinkSource {

	private final ZillowSearchUrl zillowSearchUrl;
	
	public SimpleZillowLinkSource(final String urlTemplate) {
		this.zillowSearchUrl = new ZillowSearchUrl(urlTemplate);
	}
	
	public SimpleZillowLinkSource(final ZillowSearchUrl zillowSearchUrl) {
		this.zillowSearchUrl = zillowSearchUrl;
	}
	
	public SimpleZillowLinkSource() {
		this.zillowSearchUrl = new ZillowSearchUrl();
	}
	
	@Override
	public Iterable<String> getLinks() {
		List<String> links = new ArrayList<String>();
		for (int i =1;i<=20;i++) {
			zillowSearchUrl.p=String.valueOf(i);
			String url = zillowSearchUrl.toString();
			links.add(url);
		}
		return links;
	}
}
