package com.KnappTech.sr.reporter;

import com.KnappTech.sr.model.beans.PriceHistoryStatusBean;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.ReportColumnFormat;
import com.KnappTech.view.report.ReportRow;

public class PriceHistoryStatusReporter extends RecordStatusReporter<PriceHistoryStatusBean> {

	private final PriceHistoryStatusBean ph;
	
	public PriceHistoryStatusReporter(PriceHistoryStatusBean records) {
		super(records);
		this.ph = records;
	}

	@Override
	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		try {
			row.put("id",ReportColumnFormat.ID,ph.getID());
			row.put("fh exists", ReportColumnFormat.BOOLEANFORMAT, PersistenceRegister.financial().hasStored(ph.getID()));
			row.put("beta",ReportColumnFormat.THREEDECIMAL,ph.getBeta());
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.updateReportRow(instructions,row);
	}
}