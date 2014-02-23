package com.KnappTech.sr.model.Financial;

import java.util.HashSet;

import com.KnappTech.model.PermanentLockable;
import com.KnappTech.model.Reportable;
import com.KnappTech.model.Validatable;
import com.KnappTech.sr.model.beans.FEBean;
import com.KnappTech.sr.model.beans.FSAFBean;
import com.KnappTech.sr.model.constants.StatementType;
import com.KnappTech.util.CheckedVariable;
import com.KnappTech.util.Domain;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.ReportRow;

public class FinancialStatementAtFrequency 
implements Validatable, Reportable,PermanentLockable {
	private static final long serialVersionUID = 201001161942L;
	
	private final boolean quarterly;
	private final StatementType type;
	private final HashSet<FinancialEntry> entries = new HashSet<FinancialEntry>();
	
	private transient boolean permanentlyLocked = false;
	
	public FinancialStatementAtFrequency(boolean quarterly, StatementType type) {
		this.quarterly = quarterly;
		this.type = type;
	}

	private FinancialStatementAtFrequency(FSAFBean fsaf) {
		if (fsaf==null)
			throw new NullPointerException("Given a null!");
		this.quarterly = fsaf.isQuarterly();
		this.type = fsaf.getType();
		try {
			if (fsaf.getEntries()==null)
				return;
			for (FEBean f : fsaf.getEntries()) {
				if (f==null)
					continue;
				FinancialEntry fe = FinancialEntry.create(f);
				if (fe==null)
					continue;
				entries.add(fe);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static final FinancialStatementAtFrequency create(FSAFBean fsaf) {
		if (fsaf==null)
			return null;
		if (fsaf.getType()!=null) {
			return new FinancialStatementAtFrequency(fsaf);
		}
		return null;
	}

	@Override
	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean isValid() { return true; }
	
	public FinancialEntry getData(String type) {
		if (type!=null) {
			for (FinancialEntry entry : entries) {
				if (entry.getType().equals(type)) {
					return entry;
				}
			}
		}
		return null;
	}
	
	public FinancialEntry mergeData(String rowName) {
		if (rowName!=null && !rowName.equals("")) {
			for (FinancialEntry entry : entries) {
				String t = entry.getType();
				if (t!=null && !t.equals("")) {
					if (t.equals(rowName)) {
						return entry;
					}
				} else {
					System.err.println("There is an entry in this financial statement at frequency with " +
							"no type declared.  The types may have never been loaded.");
				}
			}
			FinancialEntry entry = FinancialEntry.create(rowName);
			return entry;
		} else {
			System.err.println("Tried to merge data with a null/empty row name.");
		}
		return null;
	}
	
	public String toString() {
		String str = (isQuarterly() ? "Quarterly " : "Annual ");
		str+=getType().name()+" statement, ";
		str+=size()+" entries:\n";
		for (FinancialEntry entry : entries) {
			str += entry.toString()+"\n";
		}
		return str;
	}

	public CheckedVariable getMostRecentValue(String name, 
			String alt1, String alt2,Domain domain) {
		CheckedVariable v = getMostRecentValue(name,domain);
		if (!v.isValid()) {
			v = getMostRecentValue(alt1,domain);
			if (!v.isValid()) {
				v = getMostRecentValue(alt2,domain);
			}
		}
		return v;
	}

	public CheckedVariable getMostRecentValue(String name,Domain domain) {
		FinancialEntry entry = getData(name);
		if (entry!=null) {
			return entry.getMostRecentValue(domain);
		}
		return new CheckedVariable(domain);
	}

	public CheckedVariable getTTMSum(String name, String alt1, String alt2,Domain domain) {
		CheckedVariable v = getTTMSum(name,domain);
		if (!v.isValid()) {
			v = getTTMSum(alt1,domain);
			if (!v.isValid()) {
				v = getTTMSum(alt2,domain);
			}
		}
		return v;
	}

	public CheckedVariable getTTMSum(String name,Domain domain) {
		FinancialEntry entry = getData(name);
		if (entry!=null && entry.areAllValid(domain)) {
			return entry.getSumOfLastFour(domain);
		}
		return new CheckedVariable(domain);
	}

	public CheckedVariable getValue(String name, Domain domain, int yearsPast) {
		FinancialEntry entry = getData(name);
		if (entry!=null) {
			return entry.getValue(domain,yearsPast);
		}
		return new CheckedVariable(domain);
	}

	public CheckedVariable getLastValueOnly(String name, 
			String alt1, String alt2,Domain domain) {
		CheckedVariable v = getLastValueOnly(name,domain);
		if (!v.isValid()) {
			v = getLastValueOnly(alt1,domain);
			if (!v.isValid()) {
				v = getLastValueOnly(alt2,domain);
			}
		}
		return v;
	}

	public CheckedVariable getLastValueOnly(String name,Domain domain) {
		FinancialEntry entry = getData(name);
		if (entry!=null) {
			return entry.getLastValueOnly(domain);
		}
		return new CheckedVariable(domain);
	}

	public boolean isQuarterly() {
		return quarterly;
	}

	public StatementType getType() {
		return type;
	}
	
	public FinancialStatementAtFrequency clone() {
		FinancialStatementAtFrequency cl = new FinancialStatementAtFrequency(quarterly,type);
		cl.entries.addAll(this.entries);
		return cl;
	}
	
	public final synchronized boolean isLocked() {
		return permanentlyLocked;
	}
	
	public final synchronized boolean canEdit() {
		return !permanentlyLocked;
	}
	
	public final synchronized void permanentlyLock() {
		permanentlyLocked = true;
		for (FinancialEntry fe : entries) {
			fe.permanentlyLock();
		}
	}
	
	public final int size() {
		return entries.size();
	}

	public boolean isEmpty() {
		return entries.isEmpty();
	}
	
	public void add(FinancialEntry entry) {
		if (canEdit() && entry!=null) {
			entries.add(entry);
		}
	}

	public FinancialEntry[] getEntries() {
		return entries.toArray(new FinancialEntry[entries.size()]);
	}

	public boolean hasEntries() {
		return entries!=null && entries.size()>0;
	}
}