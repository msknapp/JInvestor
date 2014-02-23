package com.KnappTech.sr.persistence;

import java.io.Serializable;
import java.util.ArrayList;

import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.Regressable.Record;

public class PHistory implements Serializable {
	private static final long serialVersionUID = 201011071238L;
	public PHistoryEntry[] hist = null;
	public PHistory(PriceHistory ph) {
		ArrayList<PHistoryEntry> entries = new ArrayList<PHistoryEntry>();
		Record r = ph.getFirstRecord();
		while (r!=null) {
			double dv = r.getValue().doubleValue();
			int v = (int)Math.round(dv*100);
			PHistoryEntry he = new PHistoryEntry(r.getDate(),v);
			entries.add(he);
			r = ph.getFollowingRecord(r);
		}
		hist = entries.toArray(new PHistoryEntry[entries.size()]);
	}
}