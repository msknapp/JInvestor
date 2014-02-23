package com.kt.sr.test;

import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.comp.RegressionResult;
import com.KnappTech.sr.model.user.ScoringMethod;
import com.KnappTech.sr.persistence.CompanyManager;
import com.KnappTech.sr.persistence.FinancialEntryTypesManager;
import com.KnappTech.sr.persistence.FinancialHistoryManager;
import com.KnappTech.sr.persistence.PriceHistoryManager;

public class ScoringTester {
	public static void main(String[] args) {
		FinancialEntryTypesManager.Load();
		try {
			Company company = CompanyManager.getIfStored("AZN");
			PriceHistory ph = PriceHistoryManager.getIfStored("AZN");
			FinancialHistory fh = FinancialHistoryManager.getIfStored("AZN");
			RegressionResult r = company.getRegressionResultsSet().getMostAccurate();
			double score = new ScoringMethod().getScore(r, fh, ph);
			System.out.println(score);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
