package stock.download.record;

public class InMemDownloadRecordRepoTest extends AbstractDownloadRecordRepoTest {

	@Override
	public DownloadRecordRepo getRepo() {
		return new InMemDownloadRecordRepo();
	}

}
