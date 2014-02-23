package com.KnappTech.sr.reporter;

import java.util.LinkedHashSet;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.model.Reportable;
import com.KnappTech.model.ReportableSet;
import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.beans.ERStatusBean;
import com.KnappTech.sr.model.beans.PriceHistoryStatusBean;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.comp.Industry;
import com.KnappTech.sr.model.comp.RegressionResult;
import com.KnappTech.sr.model.comp.RegressionResults;
import com.KnappTech.sr.model.comp.Sector;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.sr.view.report.ReportFactory;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.Report;
import com.KnappTech.view.report.ReportColumn;
import com.KnappTech.view.report.ReportColumnFormat;
import com.KnappTech.view.report.ReportRow;

public class CompanyReporter implements Reportable, ReportableSet {
	private final Company company;
	
	public CompanyReporter(Company company) {
		if (company==null)
			throw new NullPointerException("given null company.");
		this.company = company;
	}

	@Override
	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		if (instructions==null) 
			throw new NullPointerException("null instructions.");
		if (!instructions.include(this))
			return;
		if (row==null)
			throw new NullPointerException("null row");
		try {
			row.put("id", ReportColumnFormat.ID, getCompany().getID());
			row.put("sector",ReportColumnFormat.COMMENT, Sector.getName(getCompany().getSector()));
			row.put("industry", ReportColumnFormat.COMMENT, Industry.getName(getCompany().getIndustry()));
			row.put("ph exists", ReportColumnFormat.BOOLEANFORMAT,PersistenceRegister.ph().hasStored(getCompany().getID()));
			row.put("fh exists", ReportColumnFormat.BOOLEANFORMAT,PersistenceRegister.financial().hasStored(getCompany().getID()));
			
			if (instructions.include(RegressionResult.class)) {
				try {
					RegressionResult rr = null;
					RegressionResults rrs = getCompany().getRegressionResultsSet();
					if (rrs!=null) {
						rr = getCompany().getRegressionResultsSet().getMostRecent();
						if (rr!=null) {
							PassiveUpdater.passivelyUpdatePriceHistory(getCompany(),false);
							// need to update the indicators for the regression result
							IdentifiableList<ERStatusBean, String> ers = PersistenceRegister.erStatus().getAllThatAreLoaded();
							rr.updateIndicatorsValue(ers);
							new RegressionResultReporter(rr).updateReportRow(instructions,row);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (instructions.include(PriceHistory.class)) {
				try {
					PriceHistoryStatusBean bn = PersistenceRegister.phStatus()
						.getIfStored(company.getID(), false);
					if (bn!=null)
						new PriceHistoryStatusReporter(bn).updateReportRow(instructions,row);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (!instructions.include(FinancialHistory.class))
				return;
			PassiveUpdater.passivelyUpdateFinancialHistory(getCompany());
			if (getCompany().getFinancialHistory()!=null && !getCompany().getFinancialHistory().isEmpty()) {
				new FinancialHistoryReporter(getCompany().getFinancialHistory()).updateReportRow(instructions,row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Report<?> getReport(Filter<Object> instructions) {
		Report<String> report = new Report<String>("Company");
		ReportRow<String> row;
		Report<Company> tempReport = new Report<Company>("Company");
		ReportRow<Company> tempRow = tempReport.put(getCompany());
		updateReportRow(instructions, tempRow);
		for (ReportColumn<?> column : tempReport.getColumns()) {
			row = report.put(column.getName());
			row.put("attribute",ReportColumnFormat.COMMENT,column.getName());
			row.put("value",ReportColumnFormat.COMMENT,tempRow.get(column).value());
		}
		return report;
	}

	public void makeNecessaryReportRequests(Filter<Object> instructions) {
		if (instructions.include("estimate")) {
			LinkedHashSet<String> ids = null;
			RegressionResult rr = null;
			RegressionResults rrs = getCompany().getRegressionResultsSet();
			if (rrs!=null) {
				if (ReportFactory.useMostRecentRegression()) {
					rr = getCompany().getRegressionResultsSet().getMostRecent();
				} else { // most accurate by default.
					rr = getCompany().getRegressionResultsSet().getMostAccurate();
				}
			}
			if (rr!=null) {
				ids = rr.getIndicatorIDs();
				try {
					PersistenceRegister.er().getAllThatAreStored(ids,false);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (instructions.include(PriceHistory.class)) {
			try {
				PersistenceRegister.phStatus().getIfStored(getCompany().getID(),false);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (instructions.include(FinancialHistory.class)) {
			try {
				PersistenceRegister.financial().getIfStored(getCompany().getID(),false);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public Company getCompany() {
		return company;
	}
}