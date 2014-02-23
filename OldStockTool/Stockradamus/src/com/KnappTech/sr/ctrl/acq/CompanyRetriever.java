package com.KnappTech.sr.ctrl.acq;

import java.util.LinkedHashSet;

import com.KnappTech.sr.ctrl.parse.TickerReader;
import com.KnappTech.sr.model.comp.Companies;
import com.KnappTech.sr.model.comp.Industry;
import com.KnappTech.sr.model.comp.Sector;
import com.KnappTech.sr.persist.PersistenceRegister;

public class CompanyRetriever {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// loading companies could take a while, start immediately.
		try {
			PersistenceRegister.company().loadEverythingStored();
			PersistenceRegister.industry().Load(false);
			PersistenceRegister.sector().Load(false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		TickerReader.setRange(20);
		
		System.out.println("Waiting for all companies to load...");
		Companies companies;
		try {
			companies = (Companies) PersistenceRegister.company().waitForEverythingStored();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Finished loading, starting the primary for loop.");
		for (int i = 1;i<6677;i+=20){
			System.out.println("now on row: "+i);
			TickerReader.setStartRow(i);
			companies = new Companies();
			TickerReader.setCompanies(companies);
			TickerReader.update();
			if (companies==null || companies.isEmpty()) {
				System.err.println("Warning: the companies downloaded is null/empty.");
			}
			if (companies.hasInvalidObject()) {
				System.err.println("Warning: invalid company created!");
			}
			
			try {
				PersistenceRegister.company().saveAll(companies,true);
				PersistenceRegister.industry().saveAll(Industry.getIndustries(), false);
				PersistenceRegister.sector().saveAll(Sector.getSectors(), false);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			LinkedHashSet<String> ids = companies.getIDSet();
			PersistenceRegister.company().remove(ids);
		}
	}
}
