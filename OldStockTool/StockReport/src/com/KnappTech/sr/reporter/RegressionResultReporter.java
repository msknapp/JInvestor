package com.KnappTech.sr.reporter;

import java.util.Set;

import com.KnappTech.model.Reportable;
import com.KnappTech.model.ReportableSet;
import com.KnappTech.sr.model.Regressable.EconomicRecords;
import com.KnappTech.sr.model.Regressable.Record;
import com.KnappTech.sr.model.Regressable.RecordList;
import com.KnappTech.sr.model.Regressable.RecordSet;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.comp.Indicator;
import com.KnappTech.sr.model.comp.RegressionResult;
import com.KnappTech.sr.model.user.ScoringMethod;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.sr.view.report.ReportFactory;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.Report;
import com.KnappTech.view.report.ReportColumnFormat;
import com.KnappTech.view.report.ReportRow;

public class RegressionResultReporter implements Reportable, ReportableSet {
	private final RegressionResult rr;
	
	public RegressionResultReporter(RegressionResult rr) {
		this.rr = rr;
	}

	@Override
	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		if (!instructions.include(this))
			return;
		if (rr.getItems().isEmpty())
			return;
		try {
			row.put("regression method", ReportColumnFormat.COMMENT, rr.getRegressionMethod().name());
			row.put("creation date", ReportColumnFormat.DATEFORMAT, rr.getCreationDate());
			row.put("regressand variance", ReportColumnFormat.THREEDECIMAL,rr.getRegressandVariance());
//			row.put("regression parameters", ReportColumnFormat.COMMENT,getRegressionParameters().toString());
//			row.put("regression errors", ReportColumnFormat.COMMENT, getRegressionParameterStandardErrors().toString());
			row.put("number of indicators", ReportColumnFormat.INTFORMAT, rr.getItems().size());
			row.put("r2", ReportColumnFormat.THREEDECIMAL, rr.getCoefficientOfDetermination());
			if (!instructions.include("estimate"))
				return; // nothing left to add to row.
			Company c = null;
			Object o1 = row.getObject();
			if (o1 instanceof CompanyReporter)
				c = ((CompanyReporter)o1).getCompany();
			if (o1 instanceof Company)
				c = (Company)o1;
			double lv = c.getLastKnownPrice();
			double est = rr.getUpToDateEstimate();
			double z = RegressionResult.getZ(lv,rr.getEstimateValue(),rr.getStandardDeviation());
			double p = RegressionResult.getProbabilityIncrease(z);
			double roi = RegressionResult.getROI(lv,rr.getEstimateValue());
			double stdev = rr.getStandardDeviation();
			row.put("estimate", ReportColumnFormat.USCURRENCY,est);
			row.put("estimate date", ReportColumnFormat.DATEFORMAT,rr.getEstimateDate());
			row.put("standard deviation", ReportColumnFormat.THREEDECIMAL,stdev);
			row.put("percent of value", ReportColumnFormat.PERCENT,stdev/est);
			row.put("z", ReportColumnFormat.THREEDECIMAL,z);
			row.put("probability of increase", ReportColumnFormat.PERCENT,p);
			row.put("roi", ReportColumnFormat.PERCENT,roi/100);
			if (!instructions.include("score"))
				return; // nothing left to add to row.
			ScoringMethod sm = ReportFactory.getInvestor().getScoringMethod();
			// load financial history.
			PassiveUpdater.passivelyUpdateFinancialHistory(c);
			PersistenceRegister.financial().getIfStored(c.getID(),false);
			int score = sm.getScore(rr, c.getFinancialHistory(), c.getPriceHistory());
			row.put("score", ReportColumnFormat.INTFORMAT,score);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Report<?> getReport(Filter<Object> instructions) {
		if (!instructions.include(this))
			return null;
		updateLastValues();
		Report<Indicator> report = new Report<Indicator>("Regression_Result_"+rr.getID());
		ReportRow<Indicator> row;
		for (Indicator indicator : rr.getItems()) {
			if (instructions.include(indicator)) {
				row = report.put(indicator);
				new IndicatorReporter(indicator).updateReportRow(instructions, row);
			}
		}
		return report;
	}
	
	/**
	 * Loads all of the regressable items that act as indicators for this
	 * regression results.  After these load, you will have all of the 
	 * records you need to make an estimate.
	 * @return
	 */
	public RecordSet<? extends RecordList<? extends Record>> loadRecords() {
		RecordSet<? extends RecordList<? extends Record>> records = loadEconomicRecords();
		// In future when there are other types of indicator sets, they will also be
		// loaded here, and added to the record list.
		return records;
	}
	
	/**
	 * Loads all of the economic records necessary for 
	 * this regression to make a prediction.
	 * @return
	 */
	public EconomicRecords loadEconomicRecords() {
		EconomicRecords records = new EconomicRecords();
		Set<String> indicators = rr.getERIndicatorIDs();
		try {
			records = (EconomicRecords) PersistenceRegister.er().getAllThatAreStored(indicators,false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (records==null || records.isEmpty())
			System.err.println("Warning: retrieved a null/empty set of records.");
		return records;
	}
	
	/**
	 * Uses the records to update the indicator values.  After this is
	 * done, you are ready to calculate the estimated value.
	 * @return
	 */
	public boolean updateLastValues() {
		RecordSet<? extends RecordList<? extends Record>> records = loadRecords();
		boolean allRecordsFound = (records.containsItems(rr.getIndicatorIDs()));
		Set<String> indicators = rr.getIndicatorIDs();
		Double lv = new Double(0);
		int countOfZeros = 0;
		if (!records.containsItems(indicators)) {
			if (countOfZeros>1) // one may be legitimate, but many? idk...
				System.err.format("Warning: There were %d indicators whose last value was zero.",countOfZeros);
			return allRecordsFound;
		}
		for (Indicator indicator : rr.getItems()) {
			String indicatorID = indicator.getID();
			RecordList<? extends Record> record = records.get(indicatorID);
			if (record==null)
				continue;
			lv = (indicator.isDerivative() ? 
					record.getLastDerivativeValue() :
					record.getLastValue());
			if (lv==0 && !indicator.isDerivative())
				System.out.println("Got a zero last value for "+indicator.getID()+
						", not a derivative");
			if (lv!=null)
				indicator.setLastValue(lv);
			else
				countOfZeros++;
		}
		if (countOfZeros>1) // one may be legitimate, but many? idk...
			System.err.format("Warning: There were %d indicators whose last value was zero.",countOfZeros);
		return allRecordsFound;
	}
}