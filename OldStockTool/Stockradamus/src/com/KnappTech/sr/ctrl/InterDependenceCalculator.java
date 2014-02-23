package com.KnappTech.sr.ctrl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.Regressable.PriceHistories;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.view.report.Report;
import com.KnappTech.view.report.ReportColumnFormat;
import com.KnappTech.view.report.ReportRow;

public class InterDependenceCalculator {
	
	public static void main(String[] args) {
		List<String> ids = Arrays.asList(args);
		try {
			LiteDate minDate = LiteDate.getOrCreate(Calendar.YEAR,-3);
			PriceHistories histories = (PriceHistories) PersistenceRegister.ph().getAllThatAreStored(ids,false);
			Report<PriceHistory> report = new Report<PriceHistory>("interdependence");
			ReportRow<PriceHistory> row = null;
			for (PriceHistory history : histories.getItems()) {
				try {
					row = report.put(history);
					row.put("id", ReportColumnFormat.ID, history.getID());
					double historyVarianceOfReturn= history.calculateVarianceOfReturn(minDate);
					for (PriceHistory h2 : histories.getItems()) {
						try {
//							cell = row.getCell(h2.getID());
							double covarianceOfReturn = history.calculateCovarianceOfReturn(h2, minDate);
							double ratio = covarianceOfReturn/historyVarianceOfReturn;
							row.getOrCreate(h2.getID(), ReportColumnFormat.THREEDECIMAL, ratio);
						} catch (Exception e) {
							if (e instanceof InterruptedException) 
								throw (InterruptedException)e;
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					if (e instanceof InterruptedException) 
						throw (InterruptedException)e;
					e.printStackTrace();
				}
			}
			report.bringColumnsToFront(Arrays.asList("id"));
			report.produceCSV(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}