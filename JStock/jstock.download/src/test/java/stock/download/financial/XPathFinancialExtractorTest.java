package stock.download.financial;

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
