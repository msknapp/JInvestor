package stock.download.ph;

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

import org.apache.commons.collections4.Closure;

import stock.core.TimeSpan;
import stock.download.BaseDownloader;
import stock.download.DownloadHelper;
import stock.download.record.DownloadRecordRepo;

public class PHDownloader extends BaseDownloader<String> implements Closure<String> {
	
	public PHDownloader(PHOutputBuilder<String> outputBuilder,
			DownloadHelper downloadHelper, Iterable<TimeSpan> timespans,
			DownloadRecordRepo repo) {
		super(outputBuilder, downloadHelper, timespans, repo);
	}

	@Override
	public String getId(String t) {
		return t;
	}
}