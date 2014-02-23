package com.KnappTech.sr.persistence;

import java.util.Collection;

import com.KnappTech.sr.model.comp.Companies;
import com.KnappTech.sr.model.user.Investor;
import com.KnappTech.sr.model.user.Portfolio;
import com.KnappTech.sr.model.user.ScoringMethod;

public class InvestorManager {

	
	public static Investor MichaelKnapp() {
		Collection<String> portfolio = PersistProperties.loadPortfolioTickers();
		Companies companies = null;
		try {
			companies = CompanyManager.getAllThatAreStored(portfolio);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Investor michaelKnapp =Investor.create("Michael Knapp");
		michaelKnapp.setScoringMethod(new ScoringMethod.NoShortingScoringMethod());
		michaelKnapp.getScoringMethod().setWillingToShort(false);
		michaelKnapp.depositDollars(5685.11);
		michaelKnapp.setMaxInvestedPercentInSingleStock(0.15);
		Portfolio p = new Portfolio();
		
		michaelKnapp.add(p);
		michaelKnapp.permanentlyLock();
		return michaelKnapp;
	}
}
