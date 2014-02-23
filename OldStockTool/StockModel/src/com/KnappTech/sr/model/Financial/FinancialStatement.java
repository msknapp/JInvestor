package com.KnappTech.sr.model.Financial;

import java.io.Serializable;

import com.KnappTech.model.PermanentLockable;
import com.KnappTech.model.Reportable;
import com.KnappTech.model.Validatable;
import com.KnappTech.sr.model.beans.FSBean;
import com.KnappTech.sr.model.constants.StatementType;
import com.KnappTech.util.CheckedVariable;
import com.KnappTech.util.Domain;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.ReportRow;

public class FinancialStatement implements Validatable, 
Reportable, PermanentLockable, Serializable {
	private static final long serialVersionUID = 201001162114L;
	
	private final StatementType type;
	private final FinancialStatementAtFrequency quarterlyStatement;
	private final FinancialStatementAtFrequency annualStatement;
	
	private transient boolean permanentlyLocked = false;
	
	public FinancialStatement(StatementType type) {
		this.type = type;
		this.quarterlyStatement = new FinancialStatementAtFrequency(true,type);
		this.annualStatement = new FinancialStatementAtFrequency(false,type);
	}
	
	private FinancialStatement(FSBean fs) {
		if (fs==null)
			throw new NullPointerException("Given a null!");
		this.type = fs.getType();
		if (fs.getQuarterlyStatement()!=null)
			this.quarterlyStatement = FinancialStatementAtFrequency.create(fs.getQuarterlyStatement());
		else 
			this.quarterlyStatement = new FinancialStatementAtFrequency(true,type);
		if (fs.getAnnualStatement()!=null) 
			this.annualStatement = FinancialStatementAtFrequency.create(fs.getAnnualStatement());
		else 
			this.annualStatement = new FinancialStatementAtFrequency(false,type);
	}
	
	public static final FinancialStatement create(FSBean fs) {
		if (fs.getType()!=null) {
			return new FinancialStatement(fs);
		}
		return null;
	}

	@Override
	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean isValid() { return true; }

	public CheckedVariable getMostRecentValue(String name, 
			String alt1, String alt2,Domain domain) {
		FinancialStatementAtFrequency fsaf = getQuarterlyStatement();
		if (fsaf==null) {
			try {
				fsaf = getAnnualStatement();
			} catch (Exception e) {};
		}
		CheckedVariable v = new CheckedVariable(domain);
		if (fsaf!=null) {
			try {
				v = fsaf.getMostRecentValue(name,alt1,alt2,domain);
			} catch (Exception e) {};
			if (!v.isValid()) {
				fsaf = getAnnualStatement();
				try {
					v = fsaf.getMostRecentValue(name,alt1,alt2,domain);
				} catch (Exception e) {};
			}
		}
		return v;
	}

	public CheckedVariable getMostRecentTTMSum(String name, 
			String alt1, String alt2,Domain domain) {
		FinancialStatementAtFrequency fsaf = getQuarterlyStatement();
		CheckedVariable d = new CheckedVariable(domain);
		if (fsaf!=null) {
			try {
				d = fsaf.getTTMSum(name,alt1,alt2,domain);
			} catch (Exception e) {};
		} 
		if (fsaf==null || !d.isValid()){
			try {
				fsaf = getAnnualStatement();
			} catch (Exception e) {};
			if (fsaf!=null) {
				try {
					d = fsaf.getMostRecentValue(name, alt1, alt2,domain);
				} catch (Exception e) {};
			}
		}
		return d;
	}

	public CheckedVariable getAnnualValue(String name, Domain domain, int yearsPast) {
		FinancialStatementAtFrequency fsaf = getAnnualStatement();
		CheckedVariable d = new CheckedVariable(domain);
		if (fsaf!=null) {
			try {
				d = fsaf.getValue(name,domain,yearsPast);
			} catch (Exception e) {};
		} 
		return d;
	}

	public CheckedVariable getLastValueOnly(String name, 
			String alt1, String alt2,Domain domain) {
		FinancialStatementAtFrequency fsaf = getQuarterlyStatement();
		CheckedVariable d = new CheckedVariable(domain);
		if (fsaf!=null) {
			try {
				d = fsaf.getLastValueOnly(name,alt1,alt2,domain);
			} catch (Exception e) {};
		} 
		if (fsaf==null || !d.isValid()){
			try {
				fsaf = getAnnualStatement();
			} catch (Exception e) {};
			if (fsaf!=null) {
				try {
					d = fsaf.getLastValueOnly(name, alt1, alt2,domain);
				} catch (Exception e) {};
			}
		}
		return d;
	}
	
	public String toString() {
		String str = getType().name()+" Statement:\n";
		str+=getAnnualStatement().toString();
		str+=getQuarterlyStatement().toString();
		return str;
	}

	public FinancialStatementAtFrequency getQuarterlyStatement() {
		return quarterlyStatement;
	}

	public FinancialStatementAtFrequency getAnnualStatement() {
		return annualStatement;
	}
	
	public FinancialStatementAtFrequency get(boolean quarterly) {
		if (quarterly) {
			return quarterlyStatement;
		} else {
			return annualStatement;
		}
	}

	public StatementType getType() {
		return type;
	}

	public boolean isEmpty() {
		return ((quarterlyStatement==null || quarterlyStatement.isEmpty()) && 
				(annualStatement==null || annualStatement.isEmpty()));
	}
	
	public final synchronized boolean isLocked() {
		return permanentlyLocked;
	}
	
	public final synchronized boolean canEdit() {
		return !permanentlyLocked;
	}
	
	public final synchronized void permanentlyLock() {
		permanentlyLocked = true;
		quarterlyStatement.permanentlyLock();
		annualStatement.permanentlyLock();
	}

	public boolean hasEntries() {
		return (quarterlyStatement!=null && quarterlyStatement.hasEntries()) || (annualStatement!=null && annualStatement.hasEntries());
	}

	public boolean hasAllFrequencies() {
		return quarterlyStatement!=null && quarterlyStatement.hasEntries() && annualStatement!=null && annualStatement.hasEntries();
	}
}