package stock.download;

import org.junit.Ignore;
import org.junit.Test;

public class CommandLineTest {
	
	
	@Test
	@Ignore("This test actually depends on where you put files on your file system.  It will be updated one day.")
	public void doit() {
		String[] args = new String[] {"-y"};
		DownloadCommandLine.main(args);
	}
}
