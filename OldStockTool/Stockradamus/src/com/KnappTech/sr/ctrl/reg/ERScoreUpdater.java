package com.KnappTech.sr.ctrl.reg;

import java.util.Collection;
import java.util.List;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.Regressable.ERScore;
import com.KnappTech.sr.model.Regressable.ERScoreKeeper;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.EconomicRecords;
import com.KnappTech.sr.model.comp.Companies;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.comp.RegressionResult;
import com.KnappTech.sr.model.comp.RegressionResults;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.sr.view.report.ReportFactory;

public class ERScoreUpdater {
	
	/**
	 * Responsible for updating the ER score keeper.
	 * @param args
	 */
	public static void main(String[] args) {
		ERScoreKeeper.clear();
		updateTimesUsed();
		updateDetails();
		// record today as the last update.
		ERScoreKeeper.setLastUpdate(LiteDate.getOrCreate());
		ERScoreKeeper.determineBest();// does a save in the process.
		if (args.length>0 && args[0].toLowerCase().contains("report"))
			// produce the report:
			ReportFactory.main(new String[] {"erscore"});
	}
	
	/**
	 * Updates based on times a record was used.
	 */
	private static void updateTimesUsed() {
		System.out.println("Updating ER score keeper based on times used.");
		Companies companies =null;
		for (int i = (int)'A';i<=(int)'Z';i++) {
			System.out.println("Now working on letter "+(char)i);
			try {
				companies = (Companies) PersistenceRegister.company().getEverythingStored((char)i,false);
				for (Company company : companies.getItems()) {
					try {
						RegressionResults rrs = company.getRegressionResultsSet();
						if (rrs==null)
							continue;
						for (RegressionResult rr : rrs.getItems()) {
							Collection<String> ids = rr.getIndicatorIDs();
							for (String id : ids) {
								ERScoreKeeper.incrementTimesUsed(id);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					PersistenceRegister.company().remove(company.getID());
				}
				PersistenceRegister.company().clear();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void updateDetails() {
		System.out.println("Updating ER score keeper with economic record details.");
		try {
			EconomicRecords allRecords = (EconomicRecords) PersistenceRegister.er().getEverythingStored(false);
			List<EconomicRecord> records = allRecords.getItems();
			EconomicRecord er = null;
			EconomicRecord er2 = null;
			for (int i = 0;i<records.size();i++) {
				er = records.get(i);
				ERScore sc = ERScoreKeeper.getOrCreate(er.getID());
				sc.setEndDate(er.getEndDate());
				sc.setStartDate(er.getStartDate());
				sc.setNumberOfEntries(er.size());
				for (int j = i+1;j<records.size();j++) {
					er2 = records.get(j);
					if (er.valuesHaveConstantRatio(er2.getRecords()).isPass())
						ERScoreKeeper.markSimilar(er.getID(), er2.getID());
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Finished updating ER score keeper with economic record details.");
	}
}