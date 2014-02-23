package com.KnappTech.sr.model.Financial;

import java.util.ArrayList;

import com.KnappTech.model.Reportable;
import com.KnappTech.model.Validatable;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.ReportRow;
//import com.kt.sr.persistence.FinancialEntryTypesManager;

public class FinancialEntryTypes extends ArrayList<String> 
implements Reportable, Validatable {
	private static final long serialVersionUID = 201007082008L;

//	private static final FinancialEntryTypes instance = new FinancialEntryTypes();
	
	private FinancialEntryTypes() {
	}
	
	public int index(String type) {
		if (type!=null && !type.equals("")) {
			for (int i = 0;i<this.size();i++) {
				if (get(i).equalsIgnoreCase(type)) {
					return i;
				}
			}
		} else {
			System.err.println("Tried getting an index of a " +
					"null/empty type of financial entry.");
			Thread.dumpStack();
		}
		return -1;
	}
	
	public String type(int index) {
		return get(index);
	}
	
	public boolean hasType(String type) {
		return (index(type)>=0);
	}
	
	public int mergeType(String type) {
		if (type!=null && !type.equals("")) {
			int ind = index(type);
			if (ind>=0) {
				return ind;
			} else {
				add(type);
			}
			return index(type);
		} else {
			System.err.println("Tried to merge a " +
			"null/empty type of financial entry.");
			Thread.dumpStack();
		}
		return -1;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		// TODO Auto-generated method stub
	}

	public String getType(short index) {
		if (!isEmpty()) {
			if (0<=index && index<size()) {
				return get(index);
			} else {
				System.err.println("Requested a financial entry type whose index " +
						"is outside the range of the types list.");
			}
		} else {
			System.err.println("Requested a financial entry type while the types" +
					" list is empty.  You probably need to have it loaded somewhere.");
		}
		// TODO Auto-generated method stub
		return null;
	}	
}