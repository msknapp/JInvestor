package stock.download;

import stock.core.TimeSpan;
import stock.download.ph.PHOutputBuilder;
import stock.download.record.DownloadRecordRepo;

public class IndicatorDownloader extends BaseDownloader<String> {

	public IndicatorDownloader(PHOutputBuilder<String> outputBuilder,
			DownloadHelper downloadHelper, Iterable<TimeSpan> timespans,
			DownloadRecordRepo repo) {
		super(outputBuilder, downloadHelper, timespans, repo);
	}

	@Override
	public String getId(String t) {
		return t;
	}
}