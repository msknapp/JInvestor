package com.KnappTech.sr.model.Financial;

import java.util.Collection;
//import java.util.Iterator;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadPoolExecutor;

//import com.KnappTech.model.ReportableSet;
//import com.KnappTech.sr.ctrl.parse.FinancialParser;
import com.KnappTech.sr.model.AbstractKTSet;
//import com.KnappTech.sr.persistence.FinancialEntryTypesManager;
//import com.KnappTech.sr.persistence.FinancialHistoryManager;
//import com.KnappTech.util.Filter;
//import com.KnappTech.util.KTTimer;
//import com.KnappTech.view.report.Report;
//import com.KnappTech.view.report.ReportRow;

public class FinancialHistories extends AbstractKTSet<FinancialHistory> 
//implements ReportableSet 
{
	private static final long serialVersionUID = 201001161951L;
    
    public FinancialHistories() {
    	super();
    }

	public FinancialHistories(Collection<FinancialHistory> items) {
		super(items);
	}

	@Override
	public int compareTo(String o) {
		return 0;
	}

//	@Override
//	public Report<?> getReport(Filter<Object> instructions) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
//		// TODO Auto-generated method stub
//	}

//	@Override
//	public boolean save(boolean multiThread) throws InterruptedException {
//		boolean worked = true;
//		for (FinancialHistory financialHistory : items) {
//			if (!FinancialHistoryManager.save(financialHistory, true)) {
//				worked = false;
//			}
//		}
//		return worked;
//	}
}
