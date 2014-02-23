package com.KnappTech.sr.reporter;

import com.KnappTech.model.ReportableSet;
import com.KnappTech.sr.model.comp.Companies;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.Report;
import com.KnappTech.view.report.ReportRow;

public class CompaniesReporter implements ReportableSet {
	private final Companies companies;
	
	public CompaniesReporter(Companies companies) {
		this.companies = companies;
	}

	@Override
	public Report<Company> getReport(Filter<Object> instructions) {
		Report<Company> report = new Report<Company>("Companies");
		for (Company company : companies.getItems()) {
			// TODO fix this.
			new CompanyReporter(company).makeNecessaryReportRequests(instructions);
		}
		ReportRow<Company> row;
		for (Company company : companies.getItems()) {
			if (instructions.include(company)) {
				try {
					row = report.put(company);
					if (row!=null) {
						new CompanyReporter(company).updateReportRow(instructions,row);
					}
				} catch (Exception e) {
					System.err.println("When creating report on companies, an exception was thrown by " +
							"company getReportRow function.  See details below:");
					e.printStackTrace();
				}
			}
		}
		return report;
	}
}