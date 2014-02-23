package com.KnappTech.sr.ctrl;

import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.EconomicRecords;
import com.KnappTech.sr.model.Regressable.PriceHistories;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.beans.ERStatusBean;
import com.KnappTech.sr.model.beans.PriceHistoryStatusBean;
import com.KnappTech.sr.persist.PersistenceRegister;

public class StatusUpdater {
	
	
	public static void main(String[] args) {
		try {
			PriceHistories phs = (PriceHistories) PersistenceRegister.ph().getEverythingStored('^', false);
			for (PriceHistory ph : phs.getItems()) {
				PriceHistoryStatusBean bn = new PriceHistoryStatusBean(ph);
				PersistenceRegister.phStatus().save(bn, true);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			System.out.println("Updating status for economic records ");
			EconomicRecords ers = (EconomicRecords) PersistenceRegister.er().getEverythingStored(false);
			for (EconomicRecord er : ers.getItems()) {
				ERStatusBean bn = new ERStatusBean(er);
				PersistenceRegister.erStatus().save(bn,true);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = (int)'A';i<=(int)'Z';i++) {
			try {
				System.out.println("Updating status for letter "+(char)i);
				PriceHistories phs = (PriceHistories) PersistenceRegister.ph().getEverythingStored((char)i, false);
				for (PriceHistory ph : phs.getItems()) {
					PriceHistoryStatusBean bn = new PriceHistoryStatusBean(ph);
					PersistenceRegister.phStatus().save(bn, true);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done updating all statuses.");
	}
}
