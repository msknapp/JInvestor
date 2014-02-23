package com.KnappTech.sr.ctrl.parse;


public class PHParserGoogle extends AbstractPHParserCSV {

	public PHParserGoogle() {
		this.dateFormat="d-MMM-yy";
		this.dateColumnIndex=0;
		this.priceColumnIndex=4;
		this.minCols=6;
		this.maxCols=6;
	}
	
	public String createURL() {
		String s = "http://www.google.com/finance/historical?q=NASDAQ:"+
			priceHistory.getID()+"&output=csv";
		return s;
	}
}