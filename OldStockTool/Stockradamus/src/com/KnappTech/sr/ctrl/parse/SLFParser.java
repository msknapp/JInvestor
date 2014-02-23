package com.KnappTech.sr.ctrl.parse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.constants.SourceAgency;
import com.KnappTech.util.ConnectionCreater;
import com.KnappTech.util.DefaultConnectionCreater;

public class SLFParser extends ERParser {
	public static final String SAINTLOUISFEDAPIKEY = "0fdc21ba8efcfea5c5416f3fabad4f9e";
	public static final String DATEFORMATSTRING = "yyyy-MM-dd";
	public static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat(DATEFORMATSTRING);
	public static final String startString = "VALUE";
	// Example: 42.491988-06-30
	
	protected String createURL() {
		String seriesID = record.getID();
		String url = "http://research.stlouisfed.org/fred2/data/"+seriesID+".txt";
		return url;
	}

	@Override
	public SourceAgency getSourceAgency() {
		return SourceAgency.SLF;
	}
	
	@Override
	protected boolean isConstantURL() {
		return false;
	}

	@Override
	protected void updateER() throws InterruptedException {
		BufferedReader br = null;
		String relevantData;
		LiteDate entryDate = null;
		double entryValue = 0;
		LiteDate minDate = LiteDate.getOrCreate(1988,(byte)0,(byte)1);
		
		// abridge the data to what is relevant:
		int ind = data.indexOf(startString)+startString.length();
		relevantData = data.substring(ind);
		if (relevantData.startsWith("\n"))
			relevantData=relevantData.substring(1);
		try {
			br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(relevantData.getBytes())));
			String currentLine = "";
			String[] parts = null;
			String datePart="";
			String valPart = "";
			while ((currentLine=br.readLine())!=null) {
				try {
					// remove excessive spaces:
					while (currentLine.contains("  ")) 
						currentLine = currentLine.replace("  ", " ");
					parts = currentLine.split(" ");
					if (parts==null || parts.length<2)
						continue;
					datePart = parts[0];
					entryDate=null;
					entryDate= getDateHalf(datePart);//LiteDate.getOrCreate(datePart, DATEFORMATSTRING);
					if (entryDate==null)
						continue;
					if (entryDate.before(minDate))
						continue;
					valPart = parts[1];
					if (valPart==null || valPart.length()<0)
						continue;
					entryValue=0;
					entryValue=getValueHalf(valPart);
					if (Thread.interrupted())
						throw new InterruptedException();
					try {
						record.addOrReplace(entryDate,entryValue);
					} catch (IllegalArgumentException e) {
						// do nothing, do not report it.
					}
					if (Thread.interrupted())
						throw new InterruptedException();
				} catch (Exception e) {
					if (e instanceof InterruptedException)
						throw (InterruptedException)e;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br!=null) 
				try {br.close(); } catch (IOException e) {e.printStackTrace();}
		}
	}
	
	private void defaultExceptionHandler(Exception e) {
		System.err.format("An exception was thrown by thread %s, in class %s, method %s%n" +
				"   type: %s, message: %s%n" +
				"   ", Thread.currentThread().getName(),getClass(),
				Thread.currentThread().getStackTrace()[2].getMethodName(),
				e.getClass(),e.getMessage());
	}
	
	private LiteDate getDateHalf(String dataScannerOutput){
		LiteDate tdate = null;
		int beginIndex = dataScannerOutput.length()-DATEFORMATSTRING.length();
		if (beginIndex>=0){
			String dateString = dataScannerOutput.substring(beginIndex);
			Date date;
			try {
				date = DATEFORMAT.parse(dateString);
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				if (calendar.get(Calendar.YEAR)>=LiteDate.BASEYEAR)
					tdate = LiteDate.getOrCreate(calendar);
			} catch (ParseException e) {
				defaultExceptionHandler(e);
			}
		}
		return tdate;
	}

	private double getValueHalf(String valString) {
		valString = valString.trim();
		// remove parentheses:
		if (valString.contains("(")) {
			String bf = valString.substring(0,valString.indexOf("("));
			String af = valString.substring(valString.indexOf(")")+1);
			valString = bf+af;
		}
		valString = valString.trim();
		Double d = Double.parseDouble(valString);
		return d.doubleValue();
	}

	@Override
	protected ConnectionCreater getConnectionCreater() {
		String url = createURL();
		return DefaultConnectionCreater.create(url);
	}
}