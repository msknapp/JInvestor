package com.KnappTech.sr.model;

import com.KnappTech.sr.model.Regressable.RecordSet;

public class Commodities extends RecordSet<Commodity> 
//implements ReportableSet 
{
	private static final long serialVersionUID = 201001181307L;

	@Override
	public int compareTo(String o) {
		return 0;
	}

//	@Override
//	public Report<?> getReport(Filter<Object> instructions) {
//		Report<Commodity> report = new Report<Commodity>("Commodity");
//		ReportRow<Commodity> row;
//		for (Commodity comm : items) {
//			if (instructions.include(comm)) {
//				row = report.put(comm);
//				comm.updateReportRow(instructions,row);
//			}
//		}
//		return report;
//	}

//	@Override
//	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
//		row.put("size",ReportColumnFormat.INTFORMAT,items.size());
//	}

	@Override
	public Commodities createInstance() {
		return new Commodities();
	}

//	@Override
//	public boolean save(boolean multiThread) throws InterruptedException {
//		return false;
//	}
}