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

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections4.Predicate;

public class DownloadRecordQuery {
	private String tickerSymbol;
	private String uri;
	private String system;
	private String saveFilePath;
	private String md5;
	private int[] responseCodes;
	private boolean serviceDenied;
	private Calendar attemptedAfter;
	private Calendar attemptedBefore;
	private String minAppVersion;
	private String maxAppVersion;
	private Calendar minStartDate;
	private Calendar maxStartDate;
	private Calendar minEndDate;
	private Calendar maxEndDate;
	private String responseMessageContains;
	private int maxRecords;
	private List<String> sortBy;

	public String getTickerSymbol() {
		return tickerSymbol;
	}

	public void setTickerSymbol(String tickerSymbol) {
		this.tickerSymbol = tickerSymbol;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getSaveFilePath() {
		return saveFilePath;
	}

	public void setSaveFilePath(String saveFilePath) {
		this.saveFilePath = saveFilePath;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public int[] getResponseCodes() {
		return responseCodes;
	}

	public void setResponseCodes(int[] responseCodes) {
		this.responseCodes = responseCodes;
	}

	public boolean isServiceDenied() {
		return serviceDenied;
	}

	public void setServiceDenied(boolean serviceDenied) {
		this.serviceDenied = serviceDenied;
	}

	public Calendar getAttemptedAfter() {
		return attemptedAfter;
	}

	public void setAttemptedAfter(Calendar attemptedAfter) {
		this.attemptedAfter = attemptedAfter;
	}

	public Calendar getAttemptedBefore() {
		return attemptedBefore;
	}

	public void setAttemptedBefore(Calendar attemptedBefore) {
		this.attemptedBefore = attemptedBefore;
	}

	public String getMinAppVersion() {
		return minAppVersion;
	}

	public void setMinAppVersion(String minAppVersion) {
		this.minAppVersion = minAppVersion;
	}

	public String getMaxAppVersion() {
		return maxAppVersion;
	}

	public void setMaxAppVersion(String maxAppVersion) {
		this.maxAppVersion = maxAppVersion;
	}

	public Calendar getMinStartDate() {
		return minStartDate;
	}

	public void setMinStartDate(Calendar minStartDate) {
		this.minStartDate = minStartDate;
	}

	public Calendar getMaxStartDate() {
		return maxStartDate;
	}

	public void setMaxStartDate(Calendar maxStartDate) {
		this.maxStartDate = maxStartDate;
	}

	public Calendar getMinEndDate() {
		return minEndDate;
	}

	public void setMinEndDate(Calendar minEndDate) {
		this.minEndDate = minEndDate;
	}

	public Calendar getMaxEndDate() {
		return maxEndDate;
	}

	public void setMaxEndDate(Calendar maxEndDate) {
		this.maxEndDate = maxEndDate;
	}

	public String getResponseMessageContains() {
		return responseMessageContains;
	}

	public void setResponseMessageContains(String responseMessageContains) {
		this.responseMessageContains = responseMessageContains;
	}

	public int getMaxRecords() {
		return maxRecords;
	}

	public void setMaxRecords(int maxRecords) {
		this.maxRecords = maxRecords;
	}

	public List<String> getSortBy() {
		return sortBy;
	}

	public void setSortBy(List<String> sortBy) {
		this.sortBy = sortBy;
	}

	public static final class DownloadRecordQueryPredicate implements
			Predicate<DownloadRecord> {

		private final DownloadRecordQuery query;

		public DownloadRecordQueryPredicate(final DownloadRecordQuery query) {
			this.query = query;
		}

		@Override
		public boolean evaluate(final DownloadRecord record) {
			if (query == null) {
				return true;
			}
			if (query.getUri() != null
					&& !query.getUri().equals(record.getUri())) {
				return false;
			}
			if (query.getTickerSymbol() != null
					&& !query.getTickerSymbol().equalsIgnoreCase(
							record.getId())) {
				return false;
			}
			if (query.getMinEndDate()!=null &&
					query.getMinEndDate().after(record.getQueryTimeSpan().getEnd())) {
				return false;
			}
			if (query.getMaxEndDate()!=null &&
					query.getMaxEndDate().before(record.getQueryTimeSpan().getEnd())) {
				return false;
			}

			if (query.getMinStartDate()!=null &&
					query.getMinStartDate().after(record.getQueryTimeSpan().getStart())) {
				return false;
			}
			if (query.getMaxStartDate()!=null &&
					query.getMaxStartDate().before(record.getQueryTimeSpan().getStart())) {
				return false;
			}
			if (query.getAttemptedAfter()!=null &&
					query.getAttemptedAfter().after(record.getAttemptedOn())) {
				return false;
			}
			if (query.getAttemptedBefore()!=null &&
					query.getAttemptedBefore().before(record.getAttemptedOn())) {
				return false;
			}
			if (query.getSystem()!=null && 
					!query.getSystem().equalsIgnoreCase(record.getSystem())) {
				return false;
			}
			if (query.getSaveFilePath()!=null && 
					!query.getSaveFilePath().equals(record.getSystem())) {
				return false;
			}
			if (query.getResponseCodes()!=null && 
					query.getResponseCodes().length>0 && 
					!Arrays.asList(query.getResponseCodes())
						.contains(record.getResponseCode())) {
				return false;
			}
			return true;
		}

	}

}
