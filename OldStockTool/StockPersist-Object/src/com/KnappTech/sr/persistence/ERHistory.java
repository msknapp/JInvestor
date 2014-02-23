package com.KnappTech.sr.persistence;

import java.io.Serializable;
import java.util.ArrayList;

import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.Record;
import com.KnappTech.sr.persistence.ERHistoryEntry;

public class ERHistory implements Serializable {
	private static final long serialVersionUID = 201011071238L;
	public ERHistoryEntry[] hist = null;
	
	public ERHistory() {}
	
	public ERHistory(EconomicRecord ph) {
		ArrayList<ERHistoryEntry> entries = new ArrayList<ERHistoryEntry>();
		Record r = ph.getFirstRecord();
		while (r!=null) {
			ERHistoryEntry he = new ERHistoryEntry(r.getDate(),r.getValue().doubleValue());
			entries.add(he);
			r = ph.getFollowingRecord(r);
		}
		hist = entries.toArray(new ERHistoryEntry[entries.size()]);
	}
}
