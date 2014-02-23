package stock.download.financial;

import stock.download.financial.FinancialDownloader.FinancialCombo;

public interface FinancialExtractor {
	public String transform(String html,String symbol,FinancialCombo combo);
}