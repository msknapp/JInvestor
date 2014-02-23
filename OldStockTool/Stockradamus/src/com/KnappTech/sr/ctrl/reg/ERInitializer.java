package com.KnappTech.sr.ctrl.reg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.KnappTech.model.LiteDate;
import com.KnappTech.model.UpdateFrequency;
import com.KnappTech.sr.model.Regressable.ERScoreKeeper;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.EconomicRecords;
import com.KnappTech.sr.model.Regressable.FilterResponse;
import com.KnappTech.sr.model.Regressable.Record;
import com.KnappTech.sr.model.Regressable.RecordFilter;
import com.KnappTech.sr.model.Regressable.RecordList;
import com.KnappTech.sr.model.Regressable.RecordSet;
import com.KnappTech.sr.persist.PersistenceRegister;

public class ERInitializer {

	
	public static void prepareEconomicRecords() {
		if (RegressionRuntimeSettings.isLocked())
			return; // they were already set.
 		if (RegressionRuntimeSettings.getEconomicRecordsToConsider()!=null)
			return; // they were already set.
		try {
			System.out.println("Loading all economic records.");
			EconomicRecords recordsToConsider = null;
			Collection<String> allUsed = null;
			if (RegressionRuntimeSettings.isConsiderAllRecords()) {
				allUsed = PersistenceRegister.er().getAllStoredIDs();
			} else {
				allUsed = ERScoreKeeper.getAllUsed();
			}
			recordsToConsider = (EconomicRecords) PersistenceRegister.er().getAllThatAreStored(allUsed,false);
			System.out.println("Finished loading all economic records, going to filter them now.");
			EconomicRecords approvedRecords = fixupRecords(recordsToConsider);
			approvedRecords.permanentlyLock();
			RegressionRuntimeSettings.setEconomicRecordsToConsider(approvedRecords);
			System.out.println("Finished loading and filtering all economic records.");
			RegressionRuntimeSettings.lock();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Want to filter out redundant records
	 * remove those records that are not acceptable for regression
	 * Acceptable means:
	 * - starts before max start date
	 * - ends after min end date
	 * - has minimum number of points
	 * Also want to sort by most useful to least.
	 * @param recordsToConsider
	 */
	private static EconomicRecords fixupRecords(EconomicRecords recordsToConsider) {
		List<EconomicRecord> goodRecords = filterByDates(recordsToConsider);
		// now we have removed all records that are not big enough or useful enough.
		// want to order by frequency first, then size.
		sortByUse(goodRecords);
		// now we have ordered them.
		
		// make sure that we do not have redundant records.
		removeRedundant(goodRecords);
		EconomicRecords ers = new EconomicRecords();
		ers.addInOrder(goodRecords);
		return ers;
	}
	
	private static List<EconomicRecord> filterByDates(EconomicRecords recordsToConsider) {
		System.out.println("Filtering records with bad dates, or are small/infrequent.");
		List<EconomicRecord> goodRecords = new ArrayList<EconomicRecord>();
		LiteDate maxStartDate = RegressionRuntimeSettings.getMaxRecordstartdate();
		LiteDate minEndDate = RegressionRuntimeSettings.getMinRecordEndDate();
		int minPoints = RegressionRuntimeSettings.getMinRecordPoints();
		for (EconomicRecord record : recordsToConsider.getItems()) {
			if (record==null)
				continue;
			if (record.getStartDate()==null || record.getStartDate().after(maxStartDate))
				continue;
			if (record.getEndDate()==null || record.getEndDate().before(minEndDate))
				continue;
			if (record.size()<minPoints)
				continue;
			if (record.getUpdateFrequency().isLessFrequent(UpdateFrequency.MONTHLY))
				continue;
			goodRecords.add(record);
		}
		System.out.println("Done filtering records by dates.");
		return goodRecords;
	}
	
	private static List<EconomicRecord> sortByUse(List<EconomicRecord> goodRecords) {
		System.out.println("Sorting records by how useful they are.");
		Comparator<EconomicRecord> c = new Comparator<EconomicRecord>() {
			@Override
			public int compare(EconomicRecord o1, EconomicRecord o2) {
				if (o1.getUpdateFrequency().isMoreFrequent(o2.getUpdateFrequency())) {
					return -1; // puts o1 near the top of the list.
				} else if (o2.getUpdateFrequency().isMoreFrequent(o1.getUpdateFrequency())) {
					return 1; // puts o1 near the bottom of the list.
				}
				// same frequency, compare by changes and zeros.
				int c1 = o1.getCountOfChanges();
				int c2 = o2.getCountOfChanges();
				if (c1>c2) {
					return -1; // puts o1 near the top of the list.
				} else if (c1<c2) {
					return 1; // puts o1 near the bottom of the list.
				}
				int z1 = o1.getCountOfZeroValues();
				int z2 = o2.getCountOfZeroValues();
				if (z1>z2) {
					return 1; // puts o1 near the bottom of the list.
				} else if (z1<z2) {
					return -1; // puts o1 near the top of the list.
				}
				return 0;
			}
		};
		Collections.sort(goodRecords,c);
		System.out.println("Done sorting records by how useful they are.");
		return goodRecords;
	}
	
	private static List<EconomicRecord> removeRedundant(List<EconomicRecord> goodRecords) {
		System.out.println("Removing records that appear to be redundant.");
		// make sure that we do not have redundant records.
		for (int i = goodRecords.size()-1;i>0;i--) {
			EconomicRecord r = goodRecords.get(i);
			if (r==null)
				continue;
			for (int j = 0;j<i;j++) {
				EconomicRecord r2 = goodRecords.get(j);
				if (r.valuesPracticallyMatch(r2.getRecords())) {
					EconomicRecord rem = goodRecords.remove(i);
					if (rem==r) {
						System.out.println("Removed for being redundant: "+rem.getID());
					} else if (rem==null) {
						System.err.println("  >(  <(  Failed to remove a redundant record: "+r.getID());
					} else {
						System.err.println("  >(  <(  Tried removing redundant record: "+r.getID()+
								", but actually removed: "+rem.getID());
					}
					break;
				}
			}
		}
		System.out.println("Finished removing records that appear to be redundant.");
		return goodRecords;
	}
}
