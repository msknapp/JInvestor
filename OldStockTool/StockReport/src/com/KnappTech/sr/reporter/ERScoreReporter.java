package com.KnappTech.sr.reporter;

import com.KnappTech.model.Reportable;
import com.KnappTech.sr.model.Regressable.ERScore;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.ReportColumnFormat;
import com.KnappTech.view.report.ReportRow;

public class ERScoreReporter implements Reportable {
	
	private final ERScore erScore;
	
	public ERScoreReporter(ERScore erScore) {
		this.erScore = erScore;
	}

	@Override
	public void updateReportRow(Filter<Object> instructions, ReportRow<?> row) {
		row.put("id", ReportColumnFormat.ID, erScore.getID());
		row.put("order", ReportColumnFormat.INTFORMAT, erScore.getOrder());
		row.put("start date", ReportColumnFormat.DATEFORMAT, erScore.getStartDate());
		row.put("end date", ReportColumnFormat.DATEFORMAT, erScore.getEndDate());
		row.put("times used", ReportColumnFormat.INTFORMAT, erScore.getTimesUsed());
		row.put("number of entries", ReportColumnFormat.INTFORMAT, erScore.getNumberOfEntries());
		String s = "";
		for (String m : erScore.getSimilarERs()) {
			s+=m+";";
		}
		row.put("similar", ReportColumnFormat.COMMENT, s);
		try {
			EconomicRecord er = PersistenceRegister.er().getIfStored(erScore.getID(),false);
			EconomicRecordReporter rp = new EconomicRecordReporter(er);
			rp.updateReportRow(instructions, row);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}