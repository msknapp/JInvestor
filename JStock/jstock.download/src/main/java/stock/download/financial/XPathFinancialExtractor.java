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

import stock.download.financial.FinancialDownloader.FinancialCombo;

public class XPathFinancialExtractor implements FinancialExtractor {
	private final String xpathExpression;
	// yahoo for example:
	// #yfnc_tabledata1/tbody/tr/td/table/tbody
	
	public XPathFinancialExtractor(String xpathExpression) {
		this.xpathExpression = xpathExpression;
	}

	@Override
	public String transform(String html, String symbol,
			FinancialCombo combo) {
		int i = html.indexOf("yfncsumtab");
		int j = html.indexOf("yfi_media_net");
		if (i<0 || j<0 || j<i) {
			// TODO throw exception saying it failed.
			return "";
		}
		html = "<table class=\""+html.substring(i, j);
		int k = html.lastIndexOf("table>");
		html = html.substring(0,k+6);
		return html;
	}
}