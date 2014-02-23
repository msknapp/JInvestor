package com.KnappTech.sr.view.report;

import java.util.ArrayList;
import java.util.Collection;

import com.KnappTech.model.Reportable;
import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.sr.reporter.CompanyReporter;
import com.KnappTech.sr.reporter.EconomicRecordReporter;
import com.KnappTech.sr.reporter.FinancialHistoryReporter;
import com.KnappTech.sr.reporter.PriceHistoryReporter;

public abstract class ItemGetter<T extends Reportable> {

	public static final ItemGetter<FinancialHistoryReporter> FINANCIAL = new ItemGetter<FinancialHistoryReporter>() {
		public Collection<FinancialHistoryReporter> getItems(char letter) {
			try {
				Collection<FinancialHistory> records = PersistenceRegister.financial().getEverythingStored(letter,false).getItems();
				Collection<FinancialHistoryReporter> rs = new ArrayList<FinancialHistoryReporter>();
				for (FinancialHistory r : records) {
					rs.add(new FinancialHistoryReporter(r));
				}
				return rs;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
	};

	public static final ItemGetter<CompanyReporter> COMPANY = new ItemGetter<CompanyReporter>() {
		public Collection<CompanyReporter> getItems(char letter) {
			try {
				Collection<Company> records = PersistenceRegister.company().getEverythingStored(letter,false).getItems();
				Collection<CompanyReporter> rs = new ArrayList<CompanyReporter>();
				for (Company r : records) {
					rs.add(new CompanyReporter(r));
				}
				return rs;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
	};

	public static final ItemGetter<PriceHistoryReporter> PRICE = new ItemGetter<PriceHistoryReporter>() {
		public Collection<PriceHistoryReporter> getItems(char letter) {
			try {
				Collection<PriceHistory> records = PersistenceRegister.ph().getEverythingStored(letter,false).getItems();
				Collection<PriceHistoryReporter> rs = new ArrayList<PriceHistoryReporter>();
				for (PriceHistory r : records) {
					rs.add(new PriceHistoryReporter(r));
				}
				return rs;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
	};

	public static final ItemGetter<EconomicRecordReporter> ECONOMIC = new ItemGetter<EconomicRecordReporter>() {
		public Collection<EconomicRecordReporter> getItems(char letter) {
			try {
				Collection<EconomicRecord> records = PersistenceRegister.er().getEverythingStored(letter,false).getItems();
				Collection<EconomicRecordReporter> rs = new ArrayList<EconomicRecordReporter>();
				for (EconomicRecord r : records) {
					rs.add(new EconomicRecordReporter(r));
				}
				return rs;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
	};
	
	
	
	private ItemGetter() {
		
	}
	
	public abstract Collection<T> getItems(char letter);
}
