package com.KnappTech.sr.reporter;

import com.KnappTech.model.Reportable;
import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.beans.PriceHistoryStatusBean;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.util.CheckedVariable;
import com.KnappTech.util.Domain;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.ReportColumnFormat;
import com.KnappTech.view.report.ReportRow;

public class FinancialHistoryReporter implements Reportable {
	private final FinancialHistory fh;
	
	public FinancialHistoryReporter(FinancialHistory fh) {
		this.fh = fh;
	}

	@Override
	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		if (!instructions.include(this))
			return;
		Domain domain = Domain.realDomain();
		CheckedVariable pe = new CheckedVariable(domain);
		CheckedVariable cr = new CheckedVariable(domain);
		CheckedVariable la = new CheckedVariable(domain);
		CheckedVariable pm = new CheckedVariable(domain);
		CheckedVariable dp = new CheckedVariable(domain);
		CheckedVariable roa = new CheckedVariable(domain);
		CheckedVariable roe = new CheckedVariable(domain);
		CheckedVariable mcta = new CheckedVariable(domain);
		
		PriceHistoryStatusBean phs = PersistenceRegister.phStatus().getIfLoaded(fh.getID());
		double lastValue = 0;
		double beta = 999;
		if (phs!=null) {
			lastValue = phs.getLastValue();
			beta = phs.getBeta();
		} else {
			PriceHistory ph = PersistenceRegister.ph().getIfLoaded(fh.getID());
			if (ph!=null) {
				lastValue = ph.getLastValue();
				beta = ph.getBeta();
			} 
		}
		try {
			pe = fh.getPricePerEarnings(lastValue);
			cr = fh.getCurrentRatio();
			la = fh.getTotalLiabilitiesPerTotalAssets();
			pm = fh.getProfitMargin();
			if (lastValue>0) {
				CheckedVariable div = fh.getTTMDividendPerShare();
				if (div.isValid()) {
					Double d = div.getValue()/lastValue;
					dp.setValue(d);
					if (dp.isValid() && dp.getValue()>1) {
						dp.setValue(0);
					}
				}
			}
			roa = fh.getReturnOnAssets();
			roe = fh.getReturnOnEquity();
			mcta = fh.getMarketCapitalizationPerTotalAssets(lastValue);
		} catch (Exception e) {
			System.err.println("Unhandled exception thrown while " +
					"trying to do calculations for financial history.");
		}
		row.put("id", ReportColumnFormat.ID, fh.getID());
		try {
			row.put("fh last update", ReportColumnFormat.DATEFORMAT, fh.getLastUpdate());
		} catch (Exception e) {}
		try {
			row.put("ph exists", ReportColumnFormat.BOOLEANFORMAT, PersistenceRegister.ph().hasStored(fh.getID()));
		} catch (Exception e) {}
		row.put("p/e",ReportColumnFormat.THREEDECIMAL,pe.getDoubleValue());
		row.put("CR",ReportColumnFormat.THREEDECIMAL,cr.getDoubleValue());
		row.put("L/A",ReportColumnFormat.THREEDECIMAL,la.getDoubleValue());
		row.put("PM",ReportColumnFormat.PERCENT,pm.getDoubleValue());
		row.put("div/p",ReportColumnFormat.PERCENT,dp.getDoubleValue());
		row.put("ROA",ReportColumnFormat.PERCENT,roa.getDoubleValue());
		row.put("ROE",ReportColumnFormat.PERCENT,roe.getDoubleValue());
		row.put("MC/TA", ReportColumnFormat.THREEDECIMAL, mcta.getDoubleValue());
		try {
			row.put("WFCFE (M$)",ReportColumnFormat.USCURRENCY,fh.getWeightedFCFE()/1000000);
		} catch (Exception e) {}
		try {
			row.put("WGrowth",ReportColumnFormat.PERCENT,fh.getWeightedInitialGrowth());
		} catch (Exception e) {}
		try {
			row.put("EROR",ReportColumnFormat.PERCENT,fh.getInvestorsExpectedReturnRate(
					beta));
		} catch (Exception e) {}
		try {
			row.put("Years",ReportColumnFormat.INTFORMAT,fh.getImpliedYearsRemaining(
					beta, lastValue));
		} catch (Exception e) {}
		try {
			row.put("MC (M$)",ReportColumnFormat.USCURRENCY,fh.getMarketCapitalization(
					lastValue).getDoubleValue()/1000000);
		} catch (Exception e) {}
		try {
			row.put("TE (M$)",ReportColumnFormat.USCURRENCY,new Double(
					fh.getTotalEquity().getDoubleValue()/1000000));
		} catch (Exception e) {}
		try {
			row.put("MC/FCFE",ReportColumnFormat.THREEDECIMAL,fh.getMarketCapPerFCFE(
					lastValue).getDoubleValue());
		} catch (Exception e) {}
		try {
			row.put("MC/TE",ReportColumnFormat.THREEDECIMAL,fh.getMarketCapPerTotalEquity(
					lastValue).getDoubleValue());
		} catch (Exception e) {}
	}
}