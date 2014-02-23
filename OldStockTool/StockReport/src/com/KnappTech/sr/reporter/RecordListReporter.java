package com.KnappTech.sr.reporter;

import java.util.Calendar;

import com.KnappTech.model.LiteDate;
import com.KnappTech.model.Reportable;
import com.KnappTech.model.ReportableSet;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.Regressable.Record;
import com.KnappTech.sr.model.Regressable.RecordList;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.Report;
import com.KnappTech.view.report.ReportColumnFormat;
import com.KnappTech.view.report.ReportRow;

public class RecordListReporter<TYPE extends RecordList<? extends Record>> implements Reportable,ReportableSet {
	private final TYPE records;
	
	public RecordListReporter(TYPE records) {
		this.records = records;
	}
	
	public Report<? extends Record> getReport(Filter<Object> instructions) {
		Report<Record> report = new Report<Record>("Records");
		ReportRow<Record> row;
		LiteDate date = null;
		for (int i = 0;i<records.size();i++) {
			row = report.put(records.getRecord(i));
			date = records.getDate(i);
			row.put("date",ReportColumnFormat.DATEFORMAT,date);
			row.put("value",ReportColumnFormat.THREEDECIMAL,records.getValueAtIndex(i));
		}
		return report;
	}

	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		if (instructions.include(this)) {
			try {
				double volatility = records.calculateVolatility(LiteDate.getOrCreate(Calendar.YEAR,-3));
				row.put("number of records", ReportColumnFormat.INTFORMAT,records.size());
				row.put("start date",ReportColumnFormat.DATEFORMAT,records.getStartDate());
				row.put("end date", ReportColumnFormat.DATEFORMAT,records.getEndDate());
				row.put("last successful update", ReportColumnFormat.DATEFORMAT,records.getLastSuccessfulUpdate());
				row.put("last attempted update", ReportColumnFormat.DATEFORMAT,records.getLastAttemptedUpdate());
				row.put("id", ReportColumnFormat.ID,records.getID());
				row.put("source agency",ReportColumnFormat.COMMENT,
						(records.getSourceAgency()!=null ? records.getSourceAgency().getName() : "unknown"));
				if (records.getUpdateFrequency()!=null) 
					row.put("update frequency", ReportColumnFormat.COMMENT,records.getUpdateFrequency().name());
				else 
					row.put("update frequency", ReportColumnFormat.COMMENT,
							(records instanceof PriceHistory) ? "DAILY" : "UNKNOWN");
				row.put("first value", ReportColumnFormat.USCURRENCY,records.getFirstValue());
				row.put("last value", ReportColumnFormat.USCURRENCY,records.getLastValue());
				row.put("volatility", ReportColumnFormat.USCURRENCY,volatility);
				row.put("max value", ReportColumnFormat.USCURRENCY,records.getMaxValue());
				row.put("min value", ReportColumnFormat.USCURRENCY,records.getMinValue());
				row.put("last update attempt failed", ReportColumnFormat.BOOLEANFORMAT,
						records.getLastAttemptedUpdate().after(records.getLastSuccessfulUpdate()));
				
				LiteDate maxStartDateForIndicators = (LiteDate.getOrCreate()).addYears(-12);
				boolean asd  = records.getStartDate().before(maxStartDateForIndicators);
				row.put("acceptable start date", ReportColumnFormat.BOOLEANFORMAT,asd);
				LiteDate minEndDateForIndicators = (LiteDate.getOrCreate()).addMonths(-3);
				boolean aed  = records.getEndDate().after(minEndDateForIndicators);
				row.put("acceptable end date", ReportColumnFormat.BOOLEANFORMAT,aed);
				boolean asz  = records.size()>=240;
				row.put("acceptable size", ReportColumnFormat.BOOLEANFORMAT,asz);
//				needs 240 points
				row.put("acceptable for regression", ReportColumnFormat.BOOLEANFORMAT,(asd && asz && aed));
			} catch (Exception e) {
				System.err.println("Warning: failed to produce a report row, exception thrown.");
			}
		}
	}
}