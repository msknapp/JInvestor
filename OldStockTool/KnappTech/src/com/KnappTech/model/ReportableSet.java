package com.KnappTech.model;

import com.KnappTech.util.Filter;
import com.KnappTech.view.report.Report;

public interface ReportableSet {
	public Report<?> getReport(Filter<Object> instructions);
}
