package com.KnappTech.sr.ctrl.opt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.KnappTech.sr.ctrl.PropertyManager;
import com.KnappTech.sr.model.comp.Companies;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.user.Investor;
import com.KnappTech.sr.persist.PersistenceRegister;

class OptimizationManager {
	
	public static void main(String[] args) {
		System.out.println("Loading company tickers.");
		Collection<String> companyTickers = loadPossibleTickers();
		
		Investor investor=null;
		try {
			investor = PersistenceRegister.investor().getIfStored("MichaelKnapp",false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Collection<String> investorCompanyTickers = investor.getCompanyTickers();
		for (String s : investorCompanyTickers) {
			if (!companyTickers.contains(s)) {
				companyTickers.add(s);
			}
		}
		
		// since the order matters a lot here, I use an array list.
		Companies companies = null;
		try {
			System.out.println("Loading company objects.");
			companies = (Companies) PersistenceRegister.company().getAllThatAreStored(companyTickers,false);
			System.out.println("Loading price history objects.");
			PersistenceRegister.ph().getAllThatAreStored(companyTickers,false);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Updating price histories if necessary.");
		passivelyUpdatePriceHistories(companies);
		
		System.out.println("Starting the solver.");
		Evolver evolver = new Evolver(companies,investor);
		SimulatedPortfolio solution = evolver.solve();
		solution.printDetails(investor);
	}
	
	public static void passivelyUpdatePriceHistories(Companies companies) {
		try {
			for (Company company : companies.getItems()) {
				if (company.isLocked() && company.isPriceHistorySet()) {
					company.setPriceHistory(PersistenceRegister.ph().getIfStored(company.getID(),false));
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static Collection<String> loadPossibleTickers() {
		String path = PropertyManager.getAcceptableCompaniesPath();
		BufferedReader br = null;
		Collection<String> tickers = new ArrayList<String>();
		try {
			File file = new File(path);
			
			if (file.exists()) {
				br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine())!=null) {
					tickers.add(line);
				}
			} else {
				System.err.println("Could not find file to load tickers from.");
			}
		} catch (IOException e) {
			System.err.println("IOException caught while loading possible tickers.");
		}
		return tickers;
	}
}