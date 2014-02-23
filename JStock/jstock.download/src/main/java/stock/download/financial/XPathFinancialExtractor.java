package stock.download.financial;

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