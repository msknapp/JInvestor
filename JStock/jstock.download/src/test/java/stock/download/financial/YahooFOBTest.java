package stock.download.financial;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import stock.download.OutputWithMeta;
import stock.download.financial.FinancialDownloader.FinancialCombo;

public class YahooFOBTest {

	@Test
	public void determineUrl() throws IOException {
		FinancialCombo c = new FinancialCombo(FinancialType.BALANCE,
				FinancialPeriod.ANNUALLY);
		String pathFormat = "target/tmp/yahoo/%1$s/%2$s_%3$s_%4$s.txt";
		YahooFOB yfob = new YahooFOB(pathFormat);
		String url = yfob.determineUrl("GE", c);
		Assert.assertEquals("http://finance.yahoo.com/q/is?s=GE+Balance+Sheet&annual",url);
		OutputWithMeta path = yfob.buildOutputStream("GE", c, url);
		path.getOutputStream().write("foo".getBytes());
		path.getOutputStream().close();
		File f = new File(path.getPath());
		Assert.assertTrue(f.exists());
		yfob.handleFailure("GE", c, url);
		Assert.assertFalse(f.exists());
	}
}
