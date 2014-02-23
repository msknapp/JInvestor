package com.KnappTech.sr.reporter;

import java.util.Collection;

import com.KnappTech.model.Reportable;
import com.KnappTech.model.ReportableSet;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.Report;
import com.KnappTech.view.report.ReportRow;

public abstract class RecordListsReporter<TYPE> implements ReportableSet {
	private final Collection<TYPE> records;
	
	public RecordListsReporter(Collection<TYPE> records) {
		this.records = records;
	}
	
	@Override
	public Report<TYPE> getReport(Filter<Object> instructions) {
		Report<TYPE> report = new Report<TYPE>(getReportName());
		ReportRow<TYPE> row;
		for (TYPE t : records) {
			if (instructions.include(t)) {
				try {
					row = report.put(t);
					if (row!=null) {
						getReportable(t).updateReportRow(instructions,row);
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

	protected abstract String getReportName();
	protected abstract Reportable getReportable(TYPE t);
}