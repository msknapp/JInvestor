package com.KnappTech.sr.ctrl.parse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.constants.SourceAgency;
import com.KnappTech.util.ConnectionCreater;
import com.KnappTech.util.DefaultConnectionCreater;

public class FRBParser extends ERParser {
	
	@Override
	public SourceAgency getSourceAgency() {
		return SourceAgency.FRB;
	}
	
	@Override
	protected void updateER() throws InterruptedException {
		int startIndex = data.indexOf(record.getID()+": ");
		startIndex = data.indexOf(record.getID(),startIndex+1)-1;
		int endIndex = data.indexOf(":",startIndex);
		if (endIndex<0) endIndex = data.length();
		else {
			endIndex = data.lastIndexOf("\"", endIndex);
		}
		String relevantData = data.substring(startIndex, endIndex);
		// I have narrowed it down to the relevant data.

		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
		relevantData = relevantData.replace("\"", "");
		while (relevantData.contains("  "))
			relevantData = relevantData.replace("  ", " ");
		BufferedReader br = null;
		boolean found = false;
		String sYear = "";
		int year = 0;
		String sVal = "";
		LiteDate date = null;
		double value =0;
		try {
			br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(relevantData.getBytes())));
			String line = "";
			String[] parts = null;
			while ((line = br.readLine())!=null) {
				parts = line.split(" ");
				if (!parts[0].equals(record.getID()) && found)
					break;
				if (!parts[0].equals(record.getID()))
					continue;
				found=true;
				sYear = parts[1];
				year = Integer.parseInt(sYear);
				if (year<LiteDate.BASEYEAR)
					continue;
				for (int month = 0;month<12;month++) {
					if (month+2>=parts.length)
						break;
					sVal = parts[month+2];
					sVal = sVal.replace("*", "");
					byte day = 15;
					date = LiteDate.getOrCreate(year, (byte)month,day);
					try {
						value = Double.parseDouble(sVal);
						record.addOrReplace(date, value);
					} catch (Exception e) {
						// do nothing.
					}
				}
				if (Thread.interrupted())
					throw new InterruptedException();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br!=null)
				try { br.close(); } catch (Exception e) {e.printStackTrace();}
		}
	}

	@Override
	protected boolean isConstantURL() {
		return true;
	}

	@Override
	protected ConnectionCreater getConnectionCreater() {
		String url = "http://www.federalreserve.gov/releases/g17/ipdisk/ip_sa.txt";
		return DefaultConnectionCreater.create(url);
	}
}
