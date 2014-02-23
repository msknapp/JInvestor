package com.KnappTech.sr.model.Regressable;

import java.util.Calendar;
import java.util.Collection;

import com.KnappTech.model.LiteDate;
//import com.KnappTech.model.ReportableSet;
//import com.KnappTech.sr.ctrl.parse.PriceHistoryParser;
//import com.KnappTech.sr.persistence.CompanyManager;
//import com.KnappTech.sr.persistence.PriceHistoryManager;
//import com.KnappTech.util.Filter;
import com.KnappTech.util.KTTimer;
//import com.KnappTech.view.report.ReportColumnFormat;
//import com.KnappTech.view.report.ReportRow;

public class PriceHistories extends RecordSet<PriceHistory>
{
	private static final long serialVersionUID = 201010092234L;
	
	public PriceHistories() {
		
	}
	
	public PriceHistories(Collection<PriceHistory> c) {
		super(c);
	}

	@Override
	public PriceHistories createInstance() {
		return new PriceHistories();
	}

	public PriceHistories getLetterSubSet(char letter) {
		PriceHistories letterset = new PriceHistories();
		for (PriceHistory hist : items) {
			if (hist.getID().charAt(0)==letter) {
				letterset.add(hist);
			}
		}
		return letterset;
	}

	@Override
	public String getID() {
		return "";
	}

//	@Override
//	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
//		row.put("size", ReportColumnFormat.INTFORMAT, size());
//	}

//	@Override
//	public boolean save(boolean multiThread) throws InterruptedException {
//		boolean worked = true;
//		for (PriceHistory priceHistory : items) {
//			if (!PriceHistoryManager.save(priceHistory, true))
//				worked = false;
//		}
//		return worked;
//	}
}