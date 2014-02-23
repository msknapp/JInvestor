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

public class YahooFOB extends FileFinancialOutputBuilder {

	public YahooFOB(String pathFormat) {
		super(pathFormat);
	}

	// unfortunately there is no easy way to abstract this out.

	@Override
	public String determineUrl(String stockSymbol, FinancialCombo combo) {
		// http://finance.yahoo.com/q/is?s=GE+Income+Statement&annual
		// http://finance.yahoo.com/q/is?s=GE (quarterly)
		// http://finance.yahoo.com/q/bs?s=GE+Balance+Sheet&annual
		// http://finance.yahoo.com/q/cf?s=GE+Cash+Flow&annual
		String stmt = null;
		if (combo.getType() == FinancialType.BALANCE) {
			stmt = "+Balance+Sheet";
		} else if (combo.getType() == FinancialType.INCOME) {
			stmt = "+Income+Statement";
		} else {
			stmt = "+Cash+Flow";
		}
		String trm = combo.getPeriod() == FinancialPeriod.ANNUALLY ? "&annual"
				: "";
		return String.format("http://finance.yahoo.com/q/is?s=%s%s%s",
				stockSymbol, stmt, trm);
	}

}
