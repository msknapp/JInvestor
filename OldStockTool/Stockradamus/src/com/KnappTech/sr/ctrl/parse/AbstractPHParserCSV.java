package com.KnappTech.sr.ctrl.parse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.KnappTech.model.LiteDate;
import com.KnappTech.util.KTTimer;

public abstract class AbstractPHParserCSV extends AbstractPHParser {
	protected String dateFormat = "yyyy-MM-dd";
	protected int dateColumnIndex = 0;
	protected int priceColumnIndex = 1;
	protected int maxCols = 10;
	protected int minCols = 4;
	protected String delimiter = ",";
	
	public void updatePH(String data) throws InterruptedException {
		String msg="We are trying to update the price history for "+priceHistory.getID()+", but it is taking a while...";
		KTTimer timer = new KTTimer("price update notifier",10,msg,false);
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String[] lines = data.split("[\n\r]");
		if (lines.length<2)
			System.err.println("==> Found very few lines of data from price history!");
		Date date;
		boolean firstLine = true;
		for (String line : lines) {
			if (firstLine) {
				firstLine=false;
				continue;
			}
			if (line==null || line.length()<=0)
				continue;
			String[] parts = line.split(delimiter);
			if (parts==null || parts.length<minCols || parts[0].contains("Date"))
				continue;
			if (parts.length>maxCols)
				System.err.println("==> Found excessive lines of data from price history " +
						"line, number of lines: "+parts.length);
			try {
			  	if (Thread.interrupted()) {
			  		throw new InterruptedException();
			  	}
				String dateString = parts[dateColumnIndex].trim();
				String valueString = parts[priceColumnIndex].trim();
				Calendar cal = Calendar.getInstance();
					date = sdf.parse(dateString);
				cal.setTime(date);
				LiteDate d = LiteDate.getOrCreate(cal);
				Double val = Double.parseDouble(valueString);
				priceHistory.addOrReplace(d, val);
			} catch (ParseException e) {
				
			} catch (Exception e) {
				if (e instanceof InterruptedException) 
					throw (InterruptedException)e;
			}
		}
		timer.stop();
	}
	
	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public int getDateColumnIndex() {
		return dateColumnIndex;
	}

	public void setDateColumnIndex(int dateColumnIndex) {
		this.dateColumnIndex = dateColumnIndex;
	}

	public int getPriceColumnIndex() {
		return priceColumnIndex;
	}

	public void setPriceColumnIndex(int priceColumnIndex) {
		this.priceColumnIndex = priceColumnIndex;
	}

	public int getMaxCols() {
		return maxCols;
	}

	public void setMaxCols(int maxCols) {
		this.maxCols = maxCols;
	}

	public int getMinCols() {
		return minCols;
	}

	public void setMinCols(int minCols) {
		this.minCols = minCols;
	}
}