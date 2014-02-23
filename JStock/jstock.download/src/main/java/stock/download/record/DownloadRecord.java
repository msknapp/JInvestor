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

import java.util.Calendar;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.Predicate;
import org.apache.http.HttpResponse;

import stock.core.TimeSpan;
import stock.download.OutputWithMeta;
import stock.download.util.HttpResponseUtils;

public class DownloadRecord {
	private final Calendar attemptedOn;
	private final String uri;
	private final String id;
	private int responseCode;
	private String responseMessage;
	private String system;
	private TimeSpan queryTimeSpan;
	private TimeSpan dataTimeSpan;
	private String saveFilePath;
	private String md5;
	private String appVersion;
	private boolean serviceDenied;

	public DownloadRecord(DownloadRecord original) {
		this(original,original.getAttemptedOn());
	}

	public DownloadRecord(DownloadRecord original,Calendar time) {
		this.attemptedOn = time;
		this.attemptedOn.setTime(original.getAttemptedOn().getTime());
		this.uri = original.getUri();
		this.id = original.getId();
		this.responseCode = original.getResponseCode();
		this.responseMessage = original.getResponseMessage();
		this.system = original.getSystem();
		this.queryTimeSpan = original.getQueryTimeSpan();
		this.dataTimeSpan = original.getDataTimeSpan();
		this.saveFilePath = original.getSaveFilePath();
		this.md5 = original.getMd5();
		this.appVersion = original.getAppVersion();
		this.serviceDenied = original.isServiceDenied();
	}
	
	public void updateFromResponse(HttpResponse response) {
		responseCode = HttpResponseUtils.responseCode(response);
		serviceDenied = responseCode>400 && responseCode<500 && responseCode!=404;
	}

	public void updateFromMeta(OutputWithMeta owm) {
		setSaveFilePath(owm.getPath());
	}

	public DownloadRecord(final String uri,final String tickerSymbol) {
		this.uri = uri;
		this.id = tickerSymbol;
		this.attemptedOn = Calendar.getInstance();
	}
	
	public DownloadRecord( final String uri,final String tickerSymbol, final Calendar attemptedOn) {
		this.uri = uri;
		this.id = tickerSymbol;
		this.attemptedOn = attemptedOn;
	}

	public Calendar getAttemptedOn() {
		return attemptedOn;
	}

	public String getUri() {
		return uri;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}


	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attemptedOn == null) ? 0 : attemptedOn.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DownloadRecord other = (DownloadRecord) obj;
		if (attemptedOn == null) {
			if (other.attemptedOn != null)
				return false;
		} else if (!attemptedOn.equals(other.attemptedOn))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
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

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public boolean isServiceDenied() {
		return serviceDenied;
	}

	public void setServiceDenied(boolean serviceDenied) {
		this.serviceDenied = serviceDenied;
	}

	public TimeSpan getQueryTimeSpan() {
		return queryTimeSpan;
	}

	public void setQueryTimeSpan(TimeSpan queryTimeSpan) {
		this.queryTimeSpan = queryTimeSpan;
	}

	public TimeSpan getDataTimeSpan() {
		return dataTimeSpan;
	}

	public void setDataTimeSpan(TimeSpan dataTimeSpan) {
		this.dataTimeSpan = dataTimeSpan;
	}

	public int age(TimeUnit timeUnit) {
		Calendar now=Calendar.getInstance();
		long nowTime = now.getTimeInMillis();
		long attemptTime = attemptedOn.getTimeInMillis();
		long delta = nowTime-attemptTime;
		int age = 0;
		if (timeUnit == TimeUnit.HOURS) {
			age = (int) Math.floor(delta/(1000L*60L*60L));
		} else if (timeUnit == TimeUnit.SECONDS) {
			age = (int) Math.floor(delta/(1000L));
		} else if (timeUnit == TimeUnit.MINUTES) {
			age = (int) Math.floor(delta/(1000L*60L));
		} else if (timeUnit == TimeUnit.DAYS) {
			age = (int) Math.floor(delta/(1000L*60L*60L*24L));
		} else {
			throw new IllegalArgumentException("We do not support age in "+timeUnit);
		}
		return age;
	}
	
	public static final class ChronComparator implements Comparator<DownloadRecord> {
		private final boolean ascending;
		public ChronComparator(final boolean ascending) {
			this.ascending = ascending;
		}
		
		@Override
		public int compare(DownloadRecord arg0, DownloadRecord arg1) {
			return (ascending?1:-1)*(arg0.getAttemptedOn().compareTo(arg1.getAttemptedOn()));
		}
	}
	
	public static final class SuccessfulPredicate implements Predicate<DownloadRecord> {
		private final boolean successful;
		public SuccessfulPredicate(final boolean successful) {
			this.successful = successful;
		}
		@Override
		public boolean evaluate(DownloadRecord arg0) {
			boolean wasSuccess = arg0.responseCode>=200 && arg0.responseCode<400;
			return successful ? wasSuccess : !wasSuccess;
		}
	}
}