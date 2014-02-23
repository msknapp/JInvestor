package com.KnappTech.sr.model.user;

import com.KnappTech.model.ReportableSet;
import com.KnappTech.sr.model.AbstractKTSet;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.Report;
//import com.KnappTech.view.report.ReportRow;

public class Investors extends AbstractKTSet<Investor> implements ReportableSet {
	private static final long serialVersionUID = 201001181302L;

	@Override
	public int compareTo(String o) {
		return 0;
	}

	@Override
	public Report<?> getReport(Filter<Object> instructions) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
//		// TODO Auto-generated method stub
//	}

//	@Override
//	public boolean save(boolean multiThread) throws InterruptedException {
//		return false;
//	}
}
