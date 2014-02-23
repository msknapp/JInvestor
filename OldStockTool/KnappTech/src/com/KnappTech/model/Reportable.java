package com.KnappTech.model;

import com.KnappTech.util.Filter;
import com.KnappTech.view.report.ReportRow;

/**
 * If it can be included in a report, it must implement these methods.
 * @author msknapp
 */
public interface Reportable {
	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row);
}
