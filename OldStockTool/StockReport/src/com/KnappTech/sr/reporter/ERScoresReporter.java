package com.KnappTech.sr.reporter;

import com.KnappTech.model.ReportableSet;
import com.KnappTech.sr.model.Regressable.ERScore;
import com.KnappTech.sr.model.Regressable.ERScoreKeeper;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.Report;
import com.KnappTech.view.report.ReportRow;

public class ERScoresReporter implements ReportableSet{
	
	
	public ERScoresReporter() {
		
	}
	
	@Override
	public Report<?> getReport(Filter<Object> instructions) {
		Report<ERScore> report = new Report<ERScore>("scores");
		ReportRow<ERScore> row;
		for (ERScore ers : ERScoreKeeper.getInstance().getItems()) {
			row = report.put(ers);
			new ERScoreReporter(ers).updateReportRow(instructions, row);
		}
		return report;
	}
}