package stock.download;

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

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stock.core.StockSymbol;
import stock.core.TimeSpan;
import stock.download.ph.FilePHOutputBuilder;

public class FileOutputBuilderTest {

	
	@Test
	public void determine() throws ParseException {
		String format = "http://localhost/%1$s";
		String saveDir = "/foo/bar";
		String subDirName = "lala/roro";
		
		FilePHOutputBuilder b = new FilePHOutputBuilder(format, saveDir, subDirName);
		StockSymbol stockSymbol = new StockSymbol("AAPL","foo");
		TimeSpan ts = new TimeSpan("2001-01-01");
		String url = b.determineUrl(stockSymbol, ts);
		Assert.assertEquals("http://localhost/AAPL",url);
		String md5 = "1joi2r3u908";
		String savePath = b.determineSavePath(saveDir, md5, stockSymbol);
		Assert.assertEquals("/foo/bar/lala/roro/AAPL_1joi2r3u908",savePath);
	}
}
