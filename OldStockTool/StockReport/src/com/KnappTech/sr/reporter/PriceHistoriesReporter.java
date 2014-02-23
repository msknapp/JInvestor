package com.KnappTech.sr.reporter;

import com.KnappTech.model.Reportable;
import com.KnappTech.model.ReportableSet;
import com.KnappTech.sr.model.Regressable.PriceHistories;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.Report;

public class PriceHistoriesReporter extends RecordListsReporter<PriceHistory> implements ReportableSet {
	private final PriceHistories priceHistories;
	
	public PriceHistoriesReporter(PriceHistories priceHistories) {
		super(priceHistories.getItems());
		this.priceHistories = priceHistories;
	}

	@Override
	public Report<PriceHistory> getReport(Filter<Object> instructions) {
		return super.getReport(instructions);
	}

	@Override
	protected String getReportName() {
		return "pricehistories";
	}

	@Override
	protected Reportable getReportable(PriceHistory t) {
		return new PriceHistoryReporter(t);
	}
}
