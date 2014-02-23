package com.KnappTech.sr.model.comp;

import com.KnappTech.sr.model.Asset;
import com.KnappTech.sr.model.constants.CurrencyType;
//import com.KnappTech.util.Filter;
//import com.KnappTech.view.report.Report;
//import com.KnappTech.view.report.ReportRow;
//import com.KnappTech.view.report.ReportSettings;

public class Currency implements Asset, Cloneable {
//	private static final long serialVersionUID = 201001181257L;
    private final CurrencyType type; // a good default. 
    private double quantity = 0; // by default.
    private boolean locked = false;
    private boolean permanentlyLocked = false;

    public Currency() {
    	this.quantity = 0;
    	this.type = CurrencyType.USDOLLAR;
    }

    public Currency(CurrencyType type) {
    	this.quantity = 0;
    	this.type = type;
    }
    
    public Currency(double quantity,CurrencyType type) {
    	this.quantity = quantity;
    	this.type = type;
    }
    
	@Override
	public int compareTo(String o) {
		return 0;
	}

	@Override
	public String getID() {
		return "";
	}
	
	@Override
	public boolean isValid() { return true; }

	public CurrencyType getType() {
		return type;
	}

	public void setQuantity(double quantity) {
		if (canEdit()) {
			this.quantity = quantity;
		}
	}

	public double getQuantity() {
		return quantity;
	}

	public void deposit(double amount) {
		if (canEdit()) {
			quantity+=amount;
		}
	}

	public void withdraw(double amount) {
		if (canEdit()) {
			quantity-=amount;
		}
	}

	@Override
	public Currency getValueOfShare() {
		return this;
	}
	
	public String toString() {
		return quantity+" "+type.name();
	}
	
	public Currency clone() {
		Currency c = new Currency(quantity,type);
		return c;
	}

//	@Override
//	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
//		// TODO Auto-generated method stub
//	}

	@Override
	public void lock() {
		locked = true;
	}

	@Override
	public void unlock() {
		if (!permanentlyLocked) {
			locked = false;
		}
	}

	@Override
	public boolean canEdit() {
		return !locked;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	@Override
	public void permanentlyLock() {
		permanentlyLocked=true;
		locked=true;
	}

//	@Override
//	public Report<?> getReport(Filter<Object> instructions) {
//		// TODO Auto-generated method stub
//		return null;
//	}
}
