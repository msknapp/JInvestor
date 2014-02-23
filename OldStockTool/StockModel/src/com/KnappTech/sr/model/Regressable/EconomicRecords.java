package com.KnappTech.sr.model.Regressable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class EconomicRecords extends RecordSet<EconomicRecord> 
{
	private static final long serialVersionUID = 201010092150L;

	public EconomicRecords() {
		super();
	}
	
	public EconomicRecords(Collection<EconomicRecord> records) {
		super(records);
	}
	
	@Override
	public EconomicRecords createInstance() {
		return new EconomicRecords();
	}

	public EconomicRecords subSet(RecordFilter filter) {
		Iterator<EconomicRecord> iter = items.iterator();
		EconomicRecords adjustedIndicators = new EconomicRecords();
		filter.setCurrentRecords(adjustedIndicators);
		while (iter.hasNext()) {
			adjustedIndicators.add(iter.next());
		}
		return adjustedIndicators;
	}

	public EconomicRecords subSet(RecordFilter filter,int maxIndicators) {
		Iterator<EconomicRecord> iter = items.iterator();
		EconomicRecord currentRecord;
		EconomicRecords adjustedIndicators = new EconomicRecords();
		adjustedIndicators.setMaximumSize(maxIndicators);
		filter.setCurrentRecords(adjustedIndicators);
		int count = 0;
		int numberReplaced = 0;
		while (iter.hasNext() && (adjustedIndicators.size()<maxIndicators-1 ||
				((count<400 || numberReplaced<30) && count<500))) {
			try {
				currentRecord = iter.next();
				if (filter.acceptable(currentRecord).pass())
				{
					if (adjustedIndicators.size()<maxIndicators) {
						adjustedIndicators.add(currentRecord);
					} else {
						boolean replaced = adjustedIndicators.replaceLeastFrequentlyUpdatedRecord(currentRecord)!=null;
						if (!replaced) {
							replaced = adjustedIndicators.replaceSmallestRecord(currentRecord)!=null;
						}
						if (replaced) {
							numberReplaced++;
						}
					}
				}
			} catch (Exception e) {
				System.err.println("Warning: uncaught exception in er regresser (5825683)");
				e.printStackTrace();
			}
			count++;
		}
		return adjustedIndicators;
	}
	
	public List<EconomicRecord> getRecords() {
		return Collections.unmodifiableList(items);
	}
}