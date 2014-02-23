package stock.download.record;

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
