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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;


public class InMemDownloadRecordRepo implements DownloadRecordRepo {
	
	private Map<String,List<DownloadRecord>> recordsMap = new HashMap<String,List<DownloadRecord>>();

	private final DownloadRecordRepo core;
	
	public InMemDownloadRecordRepo() {
		core = null;
	}
	
	public InMemDownloadRecordRepo(final DownloadRecordRepo core) {
		this.core = core;
	}
	
	@Override
	public void create(DownloadRecord record) {
		if (record == null || record.getUri()==null) {
			return;
		}
		if (core !=null) {
			core.create(record);
		}
		inner_create(record);
	}

	public void inner_create(DownloadRecord record) {
		List<DownloadRecord> rs = recordsMap.get(record.getUri());
		if (rs == null) {
			rs = new ArrayList<DownloadRecord>();
			recordsMap.put(record.getUri(), rs);
		}
		rs.add(record);
	}

	@Override
	public void create(List<DownloadRecord> records) {
		if (records == null || records.isEmpty()) {
			return;
		}
		if (core !=null) {
			core.create(records);
		}
		for (DownloadRecord dr : records) {
			inner_create(dr);
		}
	}

	@Override
	public DownloadRecord getLatest(String uri) {
		List<DownloadRecord> lst = recordsMap.get(uri);
		if (CollectionUtils.isEmpty(lst)) {
			if (core !=null) {
				return core.getLatest(uri);
			}
			return null;
		}
		List<DownloadRecord> records = new ArrayList<DownloadRecord>(lst);
		Collections.sort(records,new DownloadRecord.ChronComparator(false));
		return records.get(0);
	}

	@Override
	public DownloadRecord getLatestSuccess(String uri) {
		List<DownloadRecord> lst = recordsMap.get(uri);
		if (CollectionUtils.isEmpty(lst)) {
			if (core !=null) {
				return core.getLatestSuccess(uri);
			}
			return null;
		}
		List<DownloadRecord> records = new ArrayList<DownloadRecord>(lst);
		Collections.sort(records,new DownloadRecord.ChronComparator(false));
		CollectionUtils.select(records, new DownloadRecord.SuccessfulPredicate(true));
		return records.get(0);
	}

	@Override
	public List<DownloadRecord> get(DownloadRecordQuery query) {
		if (core != null) {
			return core.get(query);
		}
		List<DownloadRecord> rds = new ArrayList<DownloadRecord>();
		Predicate<DownloadRecord> predicate = new DownloadRecordQuery.DownloadRecordQueryPredicate(query);
		for (List<DownloadRecord> lst : this.recordsMap.values()) {
			rds.addAll(CollectionUtils.select(lst, predicate));
		}
		return rds;
	}

	@Override
	public void update(DownloadRecord record) {
		if (record == null) {
			return;
		}
		if (core != null) {
			core.update(record);
		}
		inner_update(record);
	}
	
	private void inner_update(DownloadRecord dr) {
		inner_delete(dr);
		inner_create(dr);
	}

	@Override
	public void delete(DownloadRecord record) {
		if (record == null) {
			return;
		}
		if (core != null) {
			core.delete(record);
		}
		inner_delete(record);
	}

	public void inner_delete(DownloadRecord record) {
		List<DownloadRecord> drs = recordsMap.get(record.getUri());
		if (drs == null) {
			return;
		}
		drs.remove(record);
	}

	@Override
	public void update(List<DownloadRecord> records) {
		if (CollectionUtils.isEmpty(records)) {
			return;
		}
		if (core != null) {
			core.update(records);
		}
		for (DownloadRecord dr : records) {
			inner_update(dr);
		}
	}


	@Override
	public void delete(List<DownloadRecord> records) {
		if (CollectionUtils.isEmpty(records)) {
			return;
		}
		if (core != null) {
			core.delete(records);
		}
		for (DownloadRecord dr : records) {
			inner_delete(dr);
		}
	}

	@Override
	public int count() {
		if (core != null) {
			return core.count();
		}
		int sz= 0;
		for (List<DownloadRecord> lst : recordsMap.values()) {
			sz+=lst.size();
		}
		return sz;
	}

	@Override
	public int count(DownloadRecordQuery query) {
		if (core != null) {
			return core.count(query);
		}
		return get(query).size();
	}
}