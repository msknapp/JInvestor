package stock.download.financial;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import stock.download.financial.FinancialDownloader.FinancialCombo;

public class XPathFinancialExtractorTest {

	
	@Test
	public void test() throws IOException {
		String html = FileUtils.readFileToString(new File("src/test/resources/yahoo_sample_financial.html"));
		String xpathExpression = "#yfnc_tabledata1/tbody/tr/td/table/tbody";
		XPathFinancialExtractor ex = new XPathFinancialExtractor(xpathExpression);
		FinancialCombo combo = new FinancialCombo(FinancialType.INCOME, FinancialPeriod.QUARTERLY);
		String s = ex.transform(html, "GE", combo);
		Assert.assertTrue(s.startsWith("<table class=\"yfncsumtab\""));
		Assert.assertTrue(s.endsWith("</table>"));
	}
}
