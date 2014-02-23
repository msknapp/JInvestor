package com.KnappTech.sr.model;

import java.util.Collection;
import java.util.List;

import com.KnappTech.model.LiteDate;
//import com.KnappTech.model.ReportableSet;
import com.KnappTech.model.TimeFrame;
import com.KnappTech.model.UpdateFrequency;
import com.KnappTech.sr.model.Regressable.Record;
import com.KnappTech.sr.model.Regressable.RecordList;
import com.KnappTech.sr.model.comp.Currency;
import com.KnappTech.sr.model.constants.SourceAgency;
//import com.KnappTech.util.Filter;
//import com.KnappTech.view.report.Report;
//import com.KnappTech.view.report.ReportRow;
//import com.KnappTech.view.report.ReportSettings;

public class Commodity extends RecordList<Record> implements Asset {
	private static final long serialVersionUID = 201001181259L;

	private Commodity(String id, SourceAgency sourceAgency) {
		super(id,sourceAgency);
	}
	
	private Commodity(String id, SourceAgency sourceAgency,Collection<Record> c) {
		super(id,sourceAgency,c);
	}
	
	private Commodity(Commodity original) {
		super(original);
	}

	public static final Commodity createInstance(String id, SourceAgency sourceAgency) {
		if (id!=null && id.length()>0 && sourceAgency!=null) {
			return new Commodity(id,sourceAgency);
		}
		return null;
	}

	public static final Commodity createInstance(String id, SourceAgency sourceAgency,Collection<Record> c) {
		if (id!=null && id.length()>0 && sourceAgency!=null) {
			return new Commodity(id,sourceAgency,c);
		}
		return null;
	}
	
	public Commodity clone() {
		return new Commodity(this);
	}
	
	@Override
	public int compareTo(String o) {
		return 0;
	}

	@Override
	public List<Double> asFrequency(UpdateFrequency updateFrequency) {
		// TODO make commodity as frequency work.
		return null;
	}

	@Override
	public void getRegressableList(LiteDate startDate, LiteDate endDate,
			UpdateFrequency updateFrequency, int offset) {
		// TODO get a regressable list 

	}

	@Override
	public boolean isMultiple() {
		// TODO is multiple
		return false;
	}

	@Override
	public boolean canBeNegative() {
		return false;
	}

	@Override
	public String getID() {
		return "";
	}

//	@Override
//	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
//		super.updateReportRow(instructions,row);
//	}
//
//	@Override
//	public Report<?> getReport(Filter<Object> instructions) {
//		return super.getReport(instructions);
//	}
	
	@Override
	public boolean isValid() { return true; }

	@Override
	public Currency getValueOfShare() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public void update() throws InterruptedException {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public Record createRecord(LiteDate date, double value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimeFrame getTimeFrame() {
		return new TimeFrame(getStartDate(),getEndDate());
	}
}
