package com.KnappTech.sr.ctrl.reg;

import java.util.ArrayList;
import java.util.List;

import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.EconomicRecords;

public class RecordQueue {
	private static final List<Rec> q = new ArrayList<Rec>();
	
	private int i = 0;
	
	static {
		// have to build the queue.
		EconomicRecords ers = RegressionRuntimeSettings.getEconomicRecordsToConsider();
		for (EconomicRecord er : ers.getItems()) {
			Rec r = new Rec();
			r.setId(er.getID());
			q.add(r);
		}
		if (RegressionRuntimeSettings.isConsiderDerivatives()) {
			for (EconomicRecord er : ers.getItems()) {
				Rec r = new Rec();
				r.setId(er.getID());
				r.setDer(true);
				q.add(r);
			}
		}
	}
	
	public RecordQueue() {
	}
	
	public boolean hasNext() {
		return (i<q.size());
	}

	public EconomicRecord next() {
		if (!hasNext())
			throw new IllegalStateException("Out of records.");
		Rec r = q.get(i++);
		EconomicRecords ers = RegressionRuntimeSettings.getEconomicRecordsToConsider();
		EconomicRecord er = ers.getUnordered(r.getId());
		if (er==null)
			throw new NullPointerException("Tried getting "+r.getId()+
					", but it could not be found in all economic records to consider.");
		if (r.isDer()) 
			er = (EconomicRecord) er.getDerivative();
		return er;
	}
	
	private static class Rec {
		private String id = "";
		private boolean der = false;
		public Rec() {
			
		}
		public void setDer(boolean der) {
			this.der = der;
		}
		public boolean isDer() {
			return der;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getId() {
			return id;
		}
	}
}