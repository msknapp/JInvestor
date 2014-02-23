package com.KnappTech.sr.ctrl.reg;

import java.util.Set;

import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.comp.RegressionResults;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.util.MethodResponse;

public class CompaniesRegresser implements Runnable {
	private final Set<String> companyIDs;
	
	public CompaniesRegresser(Set<String> companyIDs) {
		if (companyIDs==null)
			throw new NullPointerException("Must define companies.");
		this.companyIDs = companyIDs;
	}

	@Override
	public void run() {
		try {
			for (String companyID : companyIDs) {
				Company company = PersistenceRegister.company().getIfStored(companyID,false);
				if (company==null) 
					continue;
				MethodResponse m = shouldRegressCompany(company);
				if (!m.isPass())
					continue;
				CompanyRegresser companyRegresser = new CompanyRegresser(company);
				companyRegresser.run();
			}
			System.out.println("FYI: the thread "+Thread.currentThread().getName()+
					" is stopping because it is finished.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public MethodResponse shouldRegressCompany(Company company) throws InterruptedException {
		boolean doRegress = false;
		try {
			if (company==null)
				return new MethodResponse(false,company,null,"the company loaded was null.");
			if (!company.isValid())
				return new MethodResponse(false,company,null,"the company loaded was not valid.");
			loadPriceHistory(company);
			PriceHistory history = company.getPriceHistory();
			if (history==null)
				return new MethodResponse(false,history,null,"price history was null.");
			if (history.size()<=100 || history.getStartDate().after(RegressionRuntimeSettings.getMaxRecordstartdate()))
				return new MethodResponse(false,history,null,"price history is not long enough.");
			if (!history.isValid())
				return new MethodResponse(false,history,null,"price history is not valid.");
			if (company.getRegressionResultsSet()==null) {
				doRegress=true;
				return new MethodResponse(true,null,null,null);
			}
			RegressionResults rrs = company.getRegressionResultsSet();
			if (rrs.isEmpty() || !rrs.getMostRecent().getCreationDate().after(RegressionRuntimeSettings.getMinCreationDate())
					|| !rrs.getMostAccurate().isR2GreaterThan(RegressionRuntimeSettings.getMinR2())) {
				doRegress=true;
				return new MethodResponse(true,null,null,null);
			}
			return new MethodResponse(false,rrs,null,"Has a good recent regression result.");
		} catch (Exception e) {
			if (e instanceof InterruptedException)
				throw (InterruptedException)e;
			System.err.println("Warning: Uncaught exception in the fast regressers main while loop.");
			e.printStackTrace();
			return new MethodResponse(false,e,null,"uncaught exception");
		} finally {
			if (!doRegress && company!=null)
				PersistenceRegister.company().remove(company.getID());
		}
	}
	
	private void loadPriceHistory(Company company) {
		if (!company.isLocked() && !company.isPriceHistorySet()) {
			try {
				company.setPriceHistory(PersistenceRegister.ph().getIfStored(company.getID(),false));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}