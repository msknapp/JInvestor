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

public class HBaseDownloadRecordRepo implements DownloadRecordRepo {

	@Override
	public void create(DownloadRecord record) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create(List<DownloadRecord> records) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DownloadRecord getLatest(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DownloadRecord getLatestSuccess(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DownloadRecord> get(DownloadRecordQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(DownloadRecord record) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(DownloadRecord record) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(List<DownloadRecord> records) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(List<DownloadRecord> records) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int count(DownloadRecordQuery query) {
		// TODO Auto-generated method stub
		return 0;
	}

}
