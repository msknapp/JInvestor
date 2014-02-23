package com.KnappTech.sr.model.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.KnappTech.model.Lockable;
import com.KnappTech.model.Reportable;
import com.KnappTech.model.ReportableSet;
import com.KnappTech.model.Validatable;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.comp.Currencies;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.Report;
import com.KnappTech.view.report.ReportRow;

public class Portfolio 
implements Validatable, Lockable, Reportable,ReportableSet {
	private static final long serialVersionUID = 201004022104L;
	private final LinkedHashSet<Position> positions = new LinkedHashSet<Position>();
	private boolean locked = false;
	private boolean permanentlyLocked = false;

	@Override
	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		// TODO Auto-generated method stub
	}

	@Override
	public Report<?> getReport(Filter<Object> instructions) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isValid() { return true; }

	public Currencies getCurrencyInvested() {
		Currencies currencyInvested = new Currencies();
		for (Position pos : positions) {
			currencyInvested.addCurrency(pos.getCurrencyValue());
		}
		return currencyInvested;
	}

	public int getNumberOfShares(Company company) {
		int numberOfShares = 0;
		Position pos = getPosition(company);
		if (pos!=null) {
			numberOfShares += (int)Math.round(pos.getCurrentQuantity());
		}
		return numberOfShares;
	}

	public Position getPosition(Company company) {
		for (Position p : positions) {
			if (p.getAsset().getID().equals(company.getID())) {
				return p;
			}
		}
		return null;
	}

	public Collection<? extends String> getCompanyTickers() {
		HashSet<String> tickers = new HashSet<String>();
		for (Position p : positions) {
			if (p.getAsset() instanceof Company) {
				tickers.add(p.getAsset().getID());
			}
		}
		return tickers;
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