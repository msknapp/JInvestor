package stock.download;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import stock.core.TimeSpan;
import stock.download.ph.PHOutputBuilder;
import stock.download.record.DownloadRecord;
import stock.download.record.DownloadRecordRepo;
import stock.download.util.HttpResponseUtils;

public abstract class BaseDownloader<T> implements Closure<T> {
	private final Logger logger = Logger.getLogger(getClass());
	private final PHOutputBuilder<T> outputBuilder;
	private final DownloadHelper downloadHelper;
	private final Iterable<TimeSpan> timeSpans;
	private final DownloadRecordRepo repo;

	public BaseDownloader(final PHOutputBuilder<T> outputBuilder,
			final DownloadHelper downloadHelper,
			final Iterable<TimeSpan> timespans,
			final DownloadRecordRepo repo) {
		this.outputBuilder = outputBuilder;
		this.downloadHelper = downloadHelper;
		this.timeSpans = timespans;
		this.repo = repo;
	}

	@Override
	public void execute(T record) {
		if (record instanceof String) {
			record = (T) ((String)record).trim();
		}
		if (timeSpans == null) {
			execute(record,null);
		} else {
			int failCount = 0;
			List<TimeSpan> reverse = IteratorUtils.toList((Iterator<? extends TimeSpan>) timeSpans.iterator());
			Collections.reverse(reverse);
			for (final TimeSpan ts : reverse) {
				if (!execute(record, ts)) {
					failCount++;
					if (failCount>2) {
						logger.warn("aborting download for "+getId(record));
						break;
					}
				}
			} 
		}
	}
	
	private boolean execute(final T record, final TimeSpan ts) {
		OutputStream outputStream = null;
		boolean worked = false;
		DownloadRecord attemptRecord = null;
		try {
			final String url = outputBuilder.determineUrl(record,ts);
			if (DOSPrevention.shouldTry(repo,url)) {
				OutputWithMeta owm = outputBuilder.buildOutputStream(record,
						ts, url);
				outputStream = owm.getOutputStream();
				
				// by now we are almost certainly going to attempt the download:
				attemptRecord = new DownloadRecord(url,getId(record));
				attemptRecord.setQueryTimeSpan(ts);
				HttpResponse response = downloadHelper.download(url, outputStream);
				attemptRecord.updateFromResponse(response);
				attemptRecord.updateFromMeta(owm);
				worked = HttpResponseUtils.passed(response);
				if (!worked) {
					outputBuilder.handleFailure(record,
							ts, url);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (IllegalStateException e) {
			logger.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(outputStream);
			if (attemptRecord!=null) {
				repo.create(attemptRecord);
			}
		}
		if (worked) {
			System.out.println("worked for "+record);
		}
		return worked;
	}
	
	public abstract String getId(T t);
}