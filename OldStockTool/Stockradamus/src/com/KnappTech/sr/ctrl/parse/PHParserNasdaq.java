package com.KnappTech.sr.ctrl.parse;

public class PHParserNasdaq extends AbstractPHParserCSV {

	public PHParserNasdaq() {
		this.dateFormat="MM/dd/yyyy";
		this.dateColumnIndex=0;
		this.priceColumnIndex=4;
		this.minCols=6;
		this.maxCols=6;
		this.delimiter="\\s";
	}
	
	@Override
	public String createURL() {
		return "http://charting.nasdaq.com/ext/charts.dll?2-1-14-0-0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0-75-03NA000000"+priceHistory.getID()+"-&SF:4|5-WD=539-HT=395--XXCL-";
	}
}