package com.KnappTech.sr.reporter;

import com.KnappTech.model.Reportable;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.ReportColumnFormat;
import com.KnappTech.view.report.ReportRow;

public class PriceHistoryReporter extends RecordListReporter<PriceHistory> implements Reportable {
	
	private final PriceHistory ph;
	
	public PriceHistoryReporter(PriceHistory ph) {
		super(ph);
		this.ph = ph;
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