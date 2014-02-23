package com.KnappTech.sr.ctrl.reg;

import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.comp.RegressionResult;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.util.KTTimer;

public class CompanyRegresser implements Runnable {
	private final Company company;
	
	public CompanyRegresser(Company company) {
		this.company = company;
	}

	@Override
	public void run() {
		KTTimer timer = null;
		try {
			timer = new KTTimer("reg", 140, "regression for "+company.getID()+" taking too long", true);
			PriceHistory history = company.getPriceHistory();
			if (history==null)
				return;
			RegressionResult regressionResult = RegressionResultFactory.produceRegressionResult(history);
			if (regressionResult==null) {
				System.out.println("Failed to get regression result for "+company.getID());
				return;
			}
			company.addRegressionResults(regressionResult);
			timer.stop();
			if (!company.isValid()) {
				System.err.println("Warning: modified the company to be invalid!");
				return;
			}
			PersistenceRegister.company().save(company,true);
		} catch (Exception e) {
			if (e instanceof InterruptedException)
				return;
			System.err.println("Warning: Uncaught exception in the fast regressers main while loop.");
			e.printStackTrace();
		} finally {
			if (timer!=null && timer.isRunning())
				timer.stop();
		}
	}
}