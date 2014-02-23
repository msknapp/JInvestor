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


public interface DownloadRecordRepo {
	void create(DownloadRecord record);
	void create(List<DownloadRecord> records);
	DownloadRecord getLatest(String uri);
	DownloadRecord getLatestSuccess(String uri);
	List<DownloadRecord> get(DownloadRecordQuery query);
	void update(DownloadRecord record);
	void delete(DownloadRecord record);
	void update(List<DownloadRecord> records);
	void delete(List<DownloadRecord> records);
	int count();
	int count(DownloadRecordQuery query);
}
