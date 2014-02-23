package stock.download.record;

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

import java.util.List;

import org.apache.http.client.utils.DateUtils;
import org.apache.log4j.Logger;

public class LoggingDownloadRecordRepo implements DownloadRecordRepo {
	private static final Logger writer = Logger.getLogger("download.record.logger");
	
	private final DownloadRecordRepo core;
	
	public LoggingDownloadRecordRepo(final DownloadRecordRepo core) {
		this.core = core;
	}

	@Override
	public void create(DownloadRecord record) {
		log(record);
		core.create(record);
	}
	
	private void log(DownloadRecord record) {
		String delimiter = ",";
		StringBuilder sb = new StringBuilder();
		sb.append(record.getUri()).append(delimiter);
		sb.append(DateUtils.formatDate(record.getAttemptedOn().getTime(),"yyyy-MM-dd:HH:mm:ss"))
			.append(delimiter);
		sb.append(record.getResponseCode()).append(delimiter);
		sb.append(record.getSaveFilePath()).append(delimiter);
		sb.append(record.getId());
		writer.info(sb.toString());
	}

	@Override
	public void create(List<DownloadRecord> records) {
		for (DownloadRecord record : records) {
			log(record);
		}
		core.create(records);
	}

	@Override
	public DownloadRecord getLatest(String uri) {
		return core.getLatest(uri);
	}

	@Override
	public DownloadRecord getLatestSuccess(String uri) {
		return core.getLatestSuccess(uri);
	}

	@Override
	public List<DownloadRecord> get(DownloadRecordQuery query) {
		return core.get(query);
	}

	@Override
	public void update(DownloadRecord record) {
		core.update(record);
	}

	@Override
	public void delete(DownloadRecord record) {
		core.delete(record);
	}

	@Override
	public void update(List<DownloadRecord> records) {
		core.update(records);
	}

	@Override
	public void delete(List<DownloadRecord> records) {
		core.delete(records);
	}

	@Override
	public int count() {
		return core.count();
	}

	@Override
	public int count(DownloadRecordQuery query) {
		return core.count(query);
	}
}