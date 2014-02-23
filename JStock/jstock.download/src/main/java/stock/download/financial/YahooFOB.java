package stock.download.financial;

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
