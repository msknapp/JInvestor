package com.KnappTech.sr.model.Regressable;

import java.util.Collection;
import java.util.List;

import com.KnappTech.model.LiteDate;
import com.KnappTech.model.UpdateFrequency;
//import com.KnappTech.sr.ctrl.parse.PriceHistoryParser;
import com.KnappTech.sr.model.beans.PriceHistoryBean;
import com.KnappTech.sr.model.constants.SourceAgency;
//import com.KnappTech.sr.persistence.FinancialHistoryManager;
//import com.KnappTech.sr.persistence.PriceHistoryManager;
//import com.KnappTech.util.Filter;
//import com.KnappTech.view.report.Report;
//import com.KnappTech.view.report.ReportColumnFormat;
//import com.KnappTech.view.report.ReportRow;

public class PriceHistory extends RecordList<Record> {
	private static final long serialVersionUID = 201010092020L;
	private double beta = 999;

	private PriceHistory(String id) {
		super(id,SourceAgency.YAHOO);
	}

	private PriceHistory(String id,Collection<Record> c) {
		super(id,SourceAgency.YAHOO,c);
	}
	
	private PriceHistory(PriceHistory ph) {
		super(ph);
		this.beta = ph.beta;
	}
	
	public static final PriceHistory create(String id) {
		if (id!=null && id.length()>0) {
			return new PriceHistory(id);
		}
		return null;
	}
	
	public static final PriceHistory create(String id, SourceAgency sourceAgency,Collection<Record> c) {
		if (id!=null && id.length()>0 && c!=null) {
			return new PriceHistory(id,c);
		}
		return null;
	}

	public static PriceHistory create(PriceHistoryBean bean) {
		PriceHistory r = new PriceHistory(bean.getId());
		update(r,bean);
		r.setBeta(bean.getBeta());
		return r;
	}
	
	public PriceHistory clone() {
		return new PriceHistory(this);
	}

	@Override
	public boolean canBeNegative() {
		return false;
	}

//	@Override
//	public Record createRecord(LiteDate date) {
//		return Record.create(date,0);
//	}

	@Override
	public Record createRecord(LiteDate date, double value) {
		return Record.create(date,value);
	}
	
//	@Override
//	public PriceHistories createSetInstance() {
//		return new PriceHistories();
//	}
	
	@Override
	public int compareTo(String o) {
		return id.compareTo(o);
	}

	@Override
	public List<Double> asFrequency(UpdateFrequency updateFrequency) {
		return null;
	}

	@Override
	public void getRegressableList(LiteDate startDate, LiteDate endDate,
			UpdateFrequency updateFrequency, int offset) {
	}

	@Override
	public boolean isMultiple() {
		return false;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public double getBeta() {
		return beta;
	}
	
	public boolean equals(Object o) {
		if (o instanceof PriceHistory) {
			PriceHistory oph = (PriceHistory)o;
			String s = oph.getID();
			if (s!=null) {
				return s.equals(getID());
			} else {
				return getID()==null;
			}
		}
		return false;
	}
	
	public int hashCode() {
		String id = getID();
		if (id!=null) {
			return id.hashCode()+87;
		} else {
			return -1;
		}
	}
    
    
}
