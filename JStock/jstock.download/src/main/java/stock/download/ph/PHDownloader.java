package stock.download.ph;

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