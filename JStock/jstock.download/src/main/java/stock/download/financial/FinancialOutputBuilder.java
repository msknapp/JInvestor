package stock.download.financial;

import java.io.IOException;

import stock.download.OutputWithMeta;
import stock.download.financial.FinancialDownloader.FinancialCombo;

public interface FinancialOutputBuilder {
	String determineUrl(String stockSymbol,FinancialCombo combo);
	OutputWithMeta buildOutputStream(String stockSymbol,FinancialCombo combo,String url) throws IOException;
	void handleFailure(String stockSymbol,FinancialCombo combo,String url);
}