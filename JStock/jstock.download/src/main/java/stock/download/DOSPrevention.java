package stock.download;

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

import java.util.concurrent.TimeUnit;

import stock.download.record.DownloadRecord;
import stock.download.record.DownloadRecordRepo;

public class DOSPrevention {

	public static boolean shouldTry(DownloadRecordRepo repo,String url) {
		DownloadRecord lastRecord = repo.getLatest(url);
		DownloadRecord lastSuccessfulRecord = repo.getLatestSuccess(url);
		if (lastSuccessfulRecord!=null) {
			if (lastSuccessfulRecord.age(TimeUnit.DAYS)<30) {
				return false;
			}
		}
		if (lastRecord!=null) {
			if (lastRecord.isServiceDenied() && lastRecord.age(TimeUnit.HOURS)<1) {
				return false;
			}
			if (lastRecord.age(TimeUnit.DAYS)<30) {
				return false;
			}
		}
		
		return true;
	}
}