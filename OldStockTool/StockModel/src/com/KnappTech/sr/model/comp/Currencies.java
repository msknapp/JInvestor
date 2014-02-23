package com.KnappTech.sr.model.comp;

import java.util.Collection;

import com.KnappTech.sr.model.AbstractSKTSet;
import com.KnappTech.sr.model.constants.CurrencyType;
//import com.KnappTech.util.Filter;
//import com.KnappTech.view.report.ReportRow;

public class Currencies extends AbstractSKTSet<Currency> {
	private static final long serialVersionUID = 201105141314L;

	public Currencies() {
		super();
	}
	
	public Currencies(Collection<Currency> items) {
		super(items);
	}
	
	protected Currencies(Currencies otherList,String id) {
		super(otherList,id);
	}
	
	public Currencies clone() {
		return new Currencies(this,this.id);
	}
	
//	@Override
//	public boolean save(boolean multiThread) throws InterruptedException {
//		return false;
//	}

	@Override
	public int compareTo(String o) {
		return 0;
	}
	
	public double getTypeAvailable(CurrencyType type) {
		double availableAmount = 0;
		for (Currency curr : items) {
			if (curr.getType()==type) {
				availableAmount += curr.getQuantity();
			}
		}
		return availableAmount;
	}
	
	public double getDollarsQuantity() {
		return getTypeAvailable(CurrencyType.USDOLLAR);
	}

	public void addCurrency(Currency curr) {
		for (Currency currency : items) {
			if (currency.getType()==curr.getType()) {
				currency.deposit(curr.getQuantity());
				return;
			}
		}
		add(curr);
	}
	
	public void safelyAdd(Currency c) {
		boolean alreadyHas = false;
		for (Currency cc : items) {
			if (cc.getType().equals(c.getType())) {
				cc.deposit(c.getQuantity());
				alreadyHas = true;
			}
		}
		if (!alreadyHas) {
			add(c);
		}
	}

	public void safelyAddAll(Currencies provided) {
		for (Currency c : provided.items) {
			safelyAdd(c);
		}
	}

	public void addCurrencies(Currencies currencyInPF) {
		for (Currency curr : currencyInPF.items) {
			addCurrency(curr);
		}
	}

//	@Override
//	public void updateReportRow(Filter<Object> instructions, ReportRow<?> row) {
//		// TODO Auto-generated method stub
//		
//	}
}