package com.KnappTech.sr.model.Financial;

import com.KnappTech.model.KTObject;
//import com.KnappTech.util.Filter;
//import com.KnappTech.view.report.ReportRow;

public class FinancialIndicator implements KTObject {

//	private double factor = 0;
//	private double error = 0;
	
	private boolean locked = false;
	private boolean permanentlyLocked = false;
	
	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}
//	@Override
//	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
//		// TODO Auto-generated method stub
//	}
	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int compareTo(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public final synchronized boolean isLocked() {
		return locked;
	}
	
	public final synchronized boolean canEdit() {
		return !locked;
	}
	
	public final synchronized void lock() {
		locked = true;
	}
	
	public final synchronized void unlock() {
		if (!permanentlyLocked) {
			locked = false;
		}
	}
	
	public final synchronized void permanentlyLock() {
		permanentlyLocked = true;
		locked = true;
	}
	
}
