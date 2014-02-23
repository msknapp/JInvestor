package com.KnappTech.sr.ctrl;

import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.persist.PersistenceRegister;

public class PassiveUpdater {

	public static FinancialHistory passivelyUpdateFinancialHistory(Company company) {
		if (company.canEdit() && !company.isFinancialHistorySet()) {
			FinancialHistory fh;
			fh = PersistenceRegister.financial().getIfLoaded(company.getID());
			if (fh!=null) {
				company.setFinancialHistory(fh);
			}
		}
		return company.getFinancialHistory();
	}
	
	public static PriceHistory passivelyUpdatePriceHistory(Company company) {
		if (company.canEdit() && !company.isPriceHistorySet()) {
			if (PersistenceRegister.ph().getIfLoaded(company.getID())!=null) {
				company.setPriceHistory(PersistenceRegister.ph().getIfLoaded(company.getID()));
			}
		}
		return company.getPriceHistory();
	}
}