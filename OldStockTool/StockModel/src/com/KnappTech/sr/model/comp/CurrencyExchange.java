package com.KnappTech.sr.model.comp;

import java.util.List;

import com.KnappTech.model.LiteDate;
import com.KnappTech.model.TimeFrame;
import com.KnappTech.model.UpdateFrequency;
import com.KnappTech.sr.model.Asset;
import com.KnappTech.sr.model.Regressable.Record;
import com.KnappTech.sr.model.Regressable.RecordList;
import com.KnappTech.sr.model.constants.CurrencyType;
import com.KnappTech.sr.model.constants.SourceAgency;

public class CurrencyExchange extends RecordList<Record> implements Asset, Cloneable {
    private final CurrencyType type; // a good default. 
    
    public CurrencyExchange(CurrencyType type) {
    	super(type.name(),SourceAgency.YAHOO);
    	this.type = type;
    }
    
    public CurrencyExchange(CurrencyExchange original) {
    	super(original.getID(),original.getSourceAgency());
    	this.type = original.type;
    }
    
    public CurrencyExchange clone() {
    	return new CurrencyExchange(this);
    }

	@Override
	public List<Double> asFrequency(UpdateFrequency updateFrequency) {
		// TODO as frequency
		return null;
	}

	@Override
	public void getRegressableList(LiteDate startDate, LiteDate endDate,
			UpdateFrequency updateFrequency, int offset) {
		// TODO get regressable list

	}

	@Override
	public boolean isMultiple() {
		// TODO is multiple
		return false;
	}

	@Override
	public TimeFrame getTimeFrame() {
		return new TimeFrame(getStartDate(),getEndDate());
	}

	@Override
	public boolean canBeNegative() {
		return false;
	}

//	@Override
//	public void update() throws InterruptedException {
//		// TODO Auto-generated method stub
//		
//	}

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
	public Record createRecord(LiteDate date, double value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Currency getValueOfShare() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(String o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public CurrencyType getType() {
		return type;
	}
}