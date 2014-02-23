package com.kt.sr.test.junit;

import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.Record;
import com.KnappTech.sr.model.constants.SourceAgency;

public class TestEconomicRecord {
	
	@Test
	public void t() {
		Assert.assertTrue(true);
	}
//	private static EconomicRecord er1;
//	private static EconomicRecord er2;
//	
//	@BeforeClass
//	public static void setup() {
//		er1 = EconomicRecord.createInstance("TEST", SourceAgency.BLS);
//		er1.addNew(LiteDate.getOrCreate(Calendar.DAY_OF_MONTH, -8), 5);
//		er1.addNew(LiteDate.getOrCreate(Calendar.DAY_OF_MONTH, -2), 10);
//		er1.addNew(LiteDate.getOrCreate(Calendar.DAY_OF_MONTH, -1), 12);
//		er1.addNew(LiteDate.getOrCreate(), 13);
//		er1.permanentlyLock();
//		System.out.println("Done building er1.");
//		
//		Calendar p = Calendar.getInstance();
//		Calendar c = Calendar.getInstance();
//		c.set(Calendar.YEAR, 1990);
//		c.set(Calendar.MONTH, Calendar.JANUARY);
//		c.set(Calendar.DAY_OF_MONTH, 1);
//		er2 = EconomicRecord.createInstance("TEST2", SourceAgency.FRB);
//		int i=0;
//		int value = 0;
//		int dow;
//		while (c.before(p)) {
//			dow =c.get(Calendar.DAY_OF_WEEK); 
//			if (dow!=Calendar.SUNDAY && dow!=Calendar.SATURDAY) {	
//				value = -10+(i%22);
//				er2.addNew(LiteDate.getOrCreate(c), value);
//				i++;
//			}
//			c.add(Calendar.DAY_OF_MONTH, 1);
//		}
//		er2.permanentlyLock();
//		System.out.println("Done building er2.");
//	}
	
//	@Test
//	public void testGet() {
//		Assert.assertTrue(er1.getLastValue()==13);
//		Assert.assertTrue(er1.getFirstValue()==5);
//		Assert.assertTrue(er1.getMostRecentOn(
//				LiteDate.getOrCreate(Calendar.DAY_OF_MONTH, -1)).getValue()==12);
//		Assert.assertTrue(er1.getMostRecentOn(
//				LiteDate.getOrCreate(Calendar.DAY_OF_MONTH, -3)).getValue()==5);
//		Assert.assertTrue(er1.getAbsoluteMaxRecord().getValue()==13);
//		Assert.assertTrue(er1.getAbsoluteMaxRecord().getDate().
//				equals(LiteDate.getOrCreate()));
//		Assert.assertTrue(er1.getAbsoluteMaxRecord().getDate()
//				==(LiteDate.getOrCreate()));
//		Assert.assertTrue(er1.getDate(1).equals(
//				LiteDate.getOrCreate(Calendar.DAY_OF_MONTH, -2)));
//		Assert.assertTrue(er1.getDateFromEnd(2).equals(
//				LiteDate.getOrCreate(Calendar.DAY_OF_MONTH, -2)));
//	}
//	
//	@Test
//	public void testReplace() {
//		EconomicRecord erc = er2.clone();
//		Record r = erc.getFirstRecord();
//		LiteDate d = r.getDate();
//		Double v = r.getValue();
//		erc.addOrReplace(d, v+2);
//		Assert.assertTrue(erc.getFirstValue().equals(v+2));
//		try {
//			er2.addOrReplace(d, v+2);
//			Assert.fail("Modified a locked object.");
//		} catch (Exception e) {}
//		Assert.assertTrue(er2.getFirstValue().equals(v));
//		System.out.println("avg="+er2.calculateAverage());
//		System.out.println("cor="+er2.calculateCovarianceOfReturn(er2));
//		System.out.println("stdev="+er2.calculateStandardDeviation());
//		System.out.println("var="+er2.calculateVariance());
//		System.out.println("vor="+er2.calculateVarianceOfReturn());
//		System.out.println("vol="+er2.calculateVolatility());
//		System.out.println("zeros="+er2.countZeros());
//		System.out.println("lag="+er2.estimateLag(LiteDate.getOrCreate(Calendar.DAY_OF_MONTH, 3)));
//		System.out.println("nulls="+er2.getCountOfNullValues());
//		System.out.println("last der="+er2.getLastDerivativeValue());
//		System.out.println("max val="+er2.getMaxValue());
//		System.out.println("min val="+er2.getMinValue());
//		System.out.println("abs min val="+er2.getAbsoluteMinRecord().getValue());
//		System.out.println("abs max val="+er2.getAbsoluteMaxRecord().getValue());
//		System.out.println("size="+er2.size());
////		System.out.println("val on="+er2.);
////		er2.
//		
//	}
	
//	@Test 
//	public void testDer() {
//		er2.lock();
//		EconomicRecord der = (EconomicRecord) er2.getDerivative();
//		System.out.println("hi");
//	}
}