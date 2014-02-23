package stock.download;

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