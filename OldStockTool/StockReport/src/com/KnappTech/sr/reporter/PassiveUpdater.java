package com.KnappTech.sr.reporter;

import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.beans.PriceHistoryStatusBean;
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
	
	public static PriceHistory passivelyUpdatePriceHistory(Company company,boolean wholeHistory) {
		if (company.canEdit() && !company.isPriceHistorySet()) {
			if (wholeHistory) {
				if (PersistenceRegister.ph().getIfLoaded(company.getID())!=null) {
					company.setPriceHistory(PersistenceRegister.ph().getIfLoaded(company.getID()));
				}
			} else {
				// can use just the status.
				PriceHistoryStatusBean bn = PersistenceRegister.phStatus().getIfLoaded(company.getID());
				PriceHistory ph = PriceHistory.create(bn.getID());
				ph.setAdjusted(bn.isAdjusted());
				ph.setBeta(bn.getBeta());
				ph.setLastAttemptedUpdate(bn.getLastAttemptedUpdate());
				ph.setLastSuccessfulUpdate(bn.getLastSuccessfulUpdate());
				ph.addNew(bn.getEndDate(), bn.getLastValue());
				company.setPriceHistory(ph);
			}
		}
		return company.getPriceHistory();
	}
}