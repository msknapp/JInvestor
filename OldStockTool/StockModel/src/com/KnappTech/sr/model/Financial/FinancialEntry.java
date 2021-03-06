package com.KnappTech.sr.model.Financial;

import java.util.ArrayList;

import com.KnappTech.model.PermanentLockable;
import com.KnappTech.model.Reportable;
import com.KnappTech.model.Validatable;
import com.KnappTech.sr.model.beans.FEBean;
import com.KnappTech.util.CheckedVariable;
import com.KnappTech.util.Domain;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.ReportRow;

public class FinancialEntry implements Validatable, Reportable, PermanentLockable {
	private static final long serialVersionUID = 201001161949L;
	private final short index;
	private final ArrayList<Double> values = new ArrayList<Double>();
	
	private boolean locked = false;
	
	private FinancialEntry(short index) {
		if (index<0)
			throw new IllegalArgumentException("Must have a positive or zero index.");
		this.index = index;
	}
	
	private FinancialEntry(FEBean fe) {
		if (fe==null) 
			throw new NullPointerException("Given a null bean.");
		if (fe.getIndex()<0)
			throw new IllegalArgumentException("Must have a positive or zero index.");
		this.index = fe.getIndex();
		for (Double d : fe.getValues()) {
			values.add(d);
		}
	}
	
	public static final FinancialEntry create(short index) {
//		if (0<=index && index<FinancialEntryTypesManager.getSize()) {
			FinancialEntry f = new FinancialEntry(index);
			return f;
//		}
//		return null;
	}

	public static final FinancialEntry create(String rowName) {
		if (rowName!=null && rowName!="") {
			FinancialEntryType t = FinancialEntryType.getOrCreate(rowName);
			short index = (short) t.getIDValue();
			FinancialEntry f = new FinancialEntry(index);
			return f;
		} else {
			System.err.println("Tried to set the entry type to a null/empty string.");
			Thread.dumpStack();
		}
		return null;
	}

	public static FinancialEntry create(FEBean f) {
//		if (0<=f.getIndex() && f.getIndex()<FinancialEntryTypesManager.getSize()) {
			return new FinancialEntry(f);
//		}
//		return null;
	}

	@Override
	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean isValid() { return true; }


	public short getIndex() {
		return index;
	}
	
	public String getType() {
		String type = FinancialEntryType.getName(index);
		return type;
	}

	/**
	 * Does its best to consider this string value and somehow translate that
	 * into a value.  Realizes that it MUST add something to the list, because
	 * if it does not, the following values will not be considered in the 
	 * right time period.
	 * @param val
	 */
	public void parseAndAddValue(String val) {
		if (canEdit()) {
			val = removeNonNumberCharacters(val);
			Double dbl = Double.MIN_VALUE;
			boolean isThousand = false;
			boolean isMillion = false;
			boolean isBillion = false;
			if (!val.equals("")) {
				try {
					if (val.endsWith("K")) {
						val = val.substring(0,val.length()-1);
						isThousand = true;
					}
					if (val.endsWith("M")) {
						val = val.substring(0,val.length()-1);
						isMillion = true;
					}
					if (val.endsWith("B")) {
						val = val.substring(0,val.length()-1);
						isBillion = true;
					}
					dbl = Double.parseDouble(val);
					if (isThousand) {
						dbl=dbl*1000;
					}
					if (isMillion) {
						dbl=dbl*1000000;
					}
					if (isBillion) {
						dbl=dbl*1000000000;
					}
				} catch (Exception e) {
					System.err.println("Failed to parse double from "+val+" for type "+getType());
				}
			}
			values.add(dbl);
		}
	}

	private static String removeNonNumberCharacters(String val) {
		String str = "";
		int parenthesesLevel = 0;
		for (int i = 0;i<val.length();i++) {
			Character c = val.charAt(i);
			if (Character.isDigit(c) || c=='.') {
				if (parenthesesLevel<=0) {
					str+=c;
				}
			} else if (c=='(') {
				parenthesesLevel++;
			} else if (c==')') {
				parenthesesLevel--;
			} else if (c=='K' && i == val.length()-1) { // thousand
				if (parenthesesLevel<=0) {
					str+=c;
				}
			} else if (c=='M' && i == val.length()-1) { // million
				if (parenthesesLevel<=0) {
					str+=c;
				}
			} else if (c=='B' && i == val.length()-1) { // billion
				if (parenthesesLevel<=0) {
					str+=c;
				}
			} else if (c=='-' && i == 0) { // a negative number.
				if (parenthesesLevel<=0) {
					str+=c;
				}
			}
			if (parenthesesLevel<0) {
				System.err.println("have negative parentheses level while parsing value.");
			}
		}
		if (str.equals("-")) {
			str = "";
		}
		return str;
	}
	
	public String toString() {
		return getType()+": "+values.toString();
	}

	public CheckedVariable getMostRecentValue(Domain domain) {
		// the last value is the most recent
		Double dd = new Double(0);
		for (int i = size()-1;i>=0;i--) {
			dd = values.get(i);
			CheckedVariable c = new CheckedVariable(domain,dd);
			if (c.isValid()) {
				return c;
			}
		}
		return new CheckedVariable(domain);
	}
	
	public boolean areAllValid(Domain domain) {
		Double d = new Double(0);
		for (int i = 0;i<size();i++) {
			d = values.get(i);
			CheckedVariable c = new CheckedVariable(domain,d);
			if (!c.isValid()) {
				return false;
			}
		}
		return true;
	}

	public CheckedVariable getSumOfLastFour(Domain domain) {
		Double sum = new Double(0);
		Double d = new Double(0);
		if (size()>=4) {
			for (int i = size()-1;i>size()-5;i--) {
				d = values.get(i);
				CheckedVariable c = new CheckedVariable(domain,d);
				if (!c.isValid()) {
					System.err.println("Tried to get sum of last four elements, " +
							"but there are invalid values in this entry.");
				} else {
					sum += d;
				}
			}
		} else {
			System.err.println("Tried getting sum of last four, but the size " +
					"is less than four.");
		}
		CheckedVariable cv = new CheckedVariable(domain,sum);
		return cv;
	}

	public CheckedVariable getLastValueOnly(Domain domain) {
		return new CheckedVariable(domain, values.get(size()-1));
	}

	public CheckedVariable getValue(Domain domain, int yearsPast) {
		return new CheckedVariable(domain, values.get(size()-1-yearsPast));
	}
	
	public final int size() {
		return values.size();
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
		locked = true;
	}

	public boolean clear() {
		if (canEdit()) {
			this.values.clear();
			return true;
		}
		return false;
	}

	public Double[] getValues() {
		return values.toArray(new Double[values.size()]);
	}
}
