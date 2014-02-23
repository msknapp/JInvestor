package com.kt.sr.test;

import java.util.Calendar;

import com.KnappTech.model.LiteDate;
import com.KnappTech.model.UpdateFrequency;
import com.KnappTech.sr.model.Regressable.RecordList;

public class GetIndexTester {
	
	public static void main(String[] args) {
		execute();
	}
	
	private static void execute() {
		LiteDate startDate = LiteDate.getOrCreate(1980,(byte)1,(byte)4);
		UpdateFrequency[] ufs = UpdateFrequency.values();
		for (UpdateFrequency uf : ufs) {
			if (!uf.equals(UpdateFrequency.HOURLY)) {
				executeFor(startDate,startDate,uf);
			}
		}
	}

	private static void executeFor(LiteDate startDate, LiteDate testDate,UpdateFrequency uf) {
		System.out.println("Start date is "+testDate.toString()+
				" which is day in week number "+testDate.getDate().get(Calendar.DAY_OF_WEEK));
		LiteDate present = LiteDate.getOrCreate();
		int lastIndex = -1;
		
//		while (testDate.before(present)) {
//			int index = RecordList.getIndex(startDate, testDate, uf);
//			if (index!=lastIndex+1) {
//				System.err.println("Get index has failed on "+testDate.toString()+
//						" which is day in week number "+testDate.getDate().get(Calendar.DAY_OF_WEEK)+
//						" index is "+index+", last index is "+lastIndex+", update frequency is: "+uf.name());
//			}
//			lastIndex=index;
//			if (!uf.equals(UpdateFrequency.DAILY)) {
//				testDate.advance(uf,1);
//			} else {
//				testDate.advanceWeekDays(1);
//			}
//		}
	}
}