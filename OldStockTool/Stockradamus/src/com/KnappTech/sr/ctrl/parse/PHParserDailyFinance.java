package com.KnappTech.sr.ctrl.parse;

import java.util.Calendar;

import com.KnappTech.model.LiteDate;

public class PHParserDailyFinance extends AbstractPHParserCSV {

	public PHParserDailyFinance() {
		this.dateFormat="MM/dd/yy";
		this.dateColumnIndex=1;
		this.priceColumnIndex=6;
		this.minCols=6;
		this.maxCols=7;
	}
	
	public String createURL() {
		String t = priceHistory.getID().toLowerCase();
		String format = "MM/dd/yy";
		String d = LiteDate.getOrCreate(Calendar.YEAR,-1).getFormatted(format);
		String s = "http://www.dailyfinance.com/.module/download/pfweb/historical/"+
			t+"?type=2&symbol="+t+"&exch="+t+"&tf=m&gran=d&fromdate="+d;
		return s;
	}
}