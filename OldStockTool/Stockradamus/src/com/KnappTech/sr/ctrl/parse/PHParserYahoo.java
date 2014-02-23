package com.KnappTech.sr.ctrl.parse;

import java.util.Calendar;

import com.KnappTech.model.LiteDate;

public class PHParserYahoo extends AbstractPHParserCSV {
	
	public PHParserYahoo() {
		this.dateFormat="yyyy-MM-dd";
		this.dateColumnIndex=0;
		this.priceColumnIndex=6;
		this.minCols=7;
		this.maxCols=8;
	}
	
	public String createURL() {
		LiteDate lastPriceDate = null;
		if (!priceHistory.isEmpty()) {
			lastPriceDate = priceHistory.getLastDate();
		}
		if (lastPriceDate==null) {
			lastPriceDate = LiteDate.EPOCH;
		}
		LiteDate currentDate = LiteDate.getOrCreate(Calendar.DAY_OF_MONTH, 1);
		String ticker = priceHistory.getID();
		String sourceURL = "http://ichart.finance.yahoo.com/table.csv?s="+ticker+"&d="+
		currentDate.getMonth()+"&e="+currentDate.getDay()+"&f="+currentDate.getYear()+
		"&g=d&a="+lastPriceDate.getMonth()+"&b="+lastPriceDate.getDay()+
		"&c="+lastPriceDate.getYear()+"&ignore=.csv";
		return sourceURL;
	}
}