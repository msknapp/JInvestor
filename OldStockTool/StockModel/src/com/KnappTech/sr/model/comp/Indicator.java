package com.KnappTech.sr.model.comp;

import com.KnappTech.model.SemiKTO;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.Record;
import com.KnappTech.sr.model.Regressable.RecordList;
import com.KnappTech.sr.model.beans.IndicatorBean;
//import com.KnappTech.sr.persistence.EconomicRecordManager;
//import com.KnappTech.util.Filter;
//import com.KnappTech.view.report.ReportColumnFormat;
//import com.KnappTech.view.report.ReportRow;

public class Indicator implements SemiKTO {
	private static final long serialVersionUID = 201004181219L;
	
	// Stored fields:
	private final String id;
	private final double factor;
	private final double factorError;
	private final byte type; // economic record by default.
	private final boolean derivative;
	
	// optional fields (Not stored):
	private transient double lastValue = 0;
	private transient boolean lastValueSet = false;
	private transient RecordList<? extends Record> record = null;
	
	private Indicator(String id, double factor,double factorError,byte type,boolean derivative) {
		this.id = id.toUpperCase();
		this.factor = factor;
		this.factorError = factorError;
		this.type = type;
		this.derivative = derivative;
	}
	
	public static final Indicator create(String id, double factor,double factorError,
			byte type,boolean derivative) {
		if (id!=null && id.length()>0 && factor!=0 && factorError!=0 && type>=0) {
			Indicator ind = new Indicator(id,factor,factorError,type,derivative);
			return ind;
		}
		return null;
	}
	
	public static final Indicator create(RecordList<? extends Record> record,
			double factor,double error) {
		String id = record.getID();
		byte type = 1;
		if (record.getClass().equals(EconomicRecord.class)) {
			type = 0;
		}
		if (id!=null && id.length()>0 && factor!=0 && error!=0 && type>=0) {
			Indicator ind = new Indicator(id,factor,error,type,record.isDerivative());
			ind.setRecord(record);
			return ind;
		}
		return null;
	}
	
	public static final Indicator create(IndicatorBean ib) {
		return create(ib.getId(),ib.getFactor(),ib.getError(),
				ib.getType(),ib.isDerivative());
	}
	
	public void setRecord(RecordList<? extends Record> record) {
		if ((record.getClass().equals(EconomicRecord.class) && type==0) || type==1) {
			if (record.isDerivative()!=derivative) 
				throw new IllegalArgumentException("Cannot change status of derivative "+
						"in an indicator, so cannot set this record.");
			this.record = record;
			setLastValue(record.getLastValue());
		}
	}
	
	@Override
	public int hashCode() {
		return getID().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null)
			return false;
		if (!(obj instanceof Indicator))
			return false;
		return (((Indicator) obj).getID().equals(getID()));
	}
	
	public String getID() {
		return id;
	}
	
	public double getFactor() {
		return factor;
	}
	
	public double getError() {
		return factorError;
	}
	
	public void setLastValue(double lastValue) {
		this.lastValue = lastValue;
		lastValueSet=true;
	}
	
	public double getLastValue() {
		return lastValue;
	}
	
	public double getProduct() {
		if (factor==0)
			System.err.println("Warning: trying to get an indicators product, but factor is 0 for "+getID());
		if (!lastValueSet)
			// the last value was never set, it is unsafe to do this.
			throw new IllegalStateException("The last value was never set, it's unsafe to get the product" +
					" for "+getID()+", derivative: "+String.valueOf(derivative));
		return (factor*lastValue);
	}
	
	public double getProduct(double lastValue) {
		return (factor*lastValue);
	}
	
	public RecordList<? extends Record> getRecord() {
		return record;
	}

	@Override
	public int compareTo(String other) {
		return getID().compareTo(other);
	}
	
	@Override
	public String toString() {
		if (getID()==null || getID().equals("")) {
			return "Unidentified indicator";
		} else {
			String str = getID()+": Factor="+getFactor()+", Last Value="+
				(lastValueSet ? getLastValue() : "not set")+
				", Error="+getError();
			if (lastValueSet)
				str+=", Product="+getProduct();
			return str;
		}
	}

	public byte getType() {
		return type;
	}
	
	public Class<? extends RecordList<Record>> getTypeClass() {
		if (type==0) {
			return EconomicRecord.class;
		}
		return null;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	public boolean isDerivative() {
		return derivative;
	}
	
	public double getInducedError() {
		if (!lastValueSet)
			throw new IllegalStateException("Must set the last value to calculate the induced error.");
		return Math.abs(lastValue*factorError);
	}

	public double getPercentFactorError() {
		return Math.abs(factorError/factor);
	}
	
	public boolean isLastValueSet() {
		return lastValueSet;
	}
}