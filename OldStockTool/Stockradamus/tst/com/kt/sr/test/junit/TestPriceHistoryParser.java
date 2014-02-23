package com.kt.sr.test.junit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.junit.Test;
import static org.junit.Assert.*;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.ctrl.parse.PHParserYahoo;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.Regressable.Record;


public class TestPriceHistoryParser {
	PriceHistory ph = PriceHistory.create("GE");
	
	public static String getSampleUpdateString() {
		// gets the sample update from file.
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(
					TestPriceHistoryParser.class.getResourceAsStream(
					"/com/kt/sr/test/junit/SamplePriceHistoryCSV.txt")));
			StringBuilder sb = new StringBuilder();
			char c;
			int i;
			while ((i=br.read())>-1) {
				c = (char)i;
				sb.append(c);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static PriceHistory getSampleGEPriceHistory() {
		PriceHistory ph = PriceHistory.create("GE");
		PHParserYahoo.setPriceHistory(ph);
		String s = getSampleUpdateString();
		try {
			PHParserYahoo.updatePH(s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return ph;
	}
	
	@Test
	public void testNeedsUpdate() {
		PHParserYahoo.setPriceHistory(ph);
		assertTrue("never thinks an update is necessary.",
				PHParserYahoo.needsUpdate());
		ph.addNew(LiteDate.getMostRecentTradingDay(LiteDate.getOrCreate()), 2);
		assertFalse("always thinks an update is necessary.", 
				PHParserYahoo.needsUpdate());
		ph.clear();
	}
	
	@Test
	public void testDoesUpdate() {
		ph = getSampleGEPriceHistory();
		Record r2 = ph.getRecord(LiteDate.getOrCreate(2011, (byte)Calendar.FEBRUARY, (byte)16));
		assertTrue("value was: "+r2.getValue().doubleValue(),21.44== r2.getValue().doubleValue());
		Record r =ph.getRecord(LiteDate.getOrCreate(2010, (byte)Calendar.NOVEMBER, (byte)12));
		assertTrue("value was: "+r.getValue().doubleValue(),16.12== r.getValue().doubleValue());
		Record r3 = ph.getRecord(LiteDate.getOrCreate(2011, (byte)Calendar.FEBRUARY, (byte)19));
		assertNull(r3);
		assertNotNull(ph.getStartDate());
		assertNotNull(ph.getEndDate());
		assertTrue(ph.getEndDate().after(ph.getStartDate()));
//		assertTrue(ph.isValid());
//		assertNotNull(ph.getLastAttemptedUpdate());
//		assertNotNull(ph.getLastSuccessfulUpdate());
	}
}
