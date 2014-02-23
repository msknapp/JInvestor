package com.KnappTech.sr.reporter;

import com.KnappTech.model.LiteDate;
import com.KnappTech.model.Reportable;
import com.KnappTech.sr.model.beans.RecordStatusBean;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.ReportColumnFormat;
import com.KnappTech.view.report.ReportRow;

public class RecordStatusReporter<TYPE extends RecordStatusBean> implements Reportable  {
	private final TYPE records;

	public RecordStatusReporter(TYPE records) {
		this.records = records;
	}

	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		if (instructions.include(this)) {
			try {
				double volatility = records.getVolatility();
				row.put("number of records", ReportColumnFormat.INTFORMAT,records.getSize());
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
					row.put("update frequency", ReportColumnFormat.COMMENT,"UNKNOWN");
				row.put("last value", ReportColumnFormat.USCURRENCY,records.getLastValue());
				row.put("volatility", ReportColumnFormat.USCURRENCY,volatility);
				row.put("last update attempt failed", ReportColumnFormat.BOOLEANFORMAT,
						records.getLastAttemptedUpdate().after(records.getLastSuccessfulUpdate()));
				
				LiteDate maxStartDateForIndicators = (LiteDate.getOrCreate()).addYears(-12);
				boolean asd  = records.getStartDate().before(maxStartDateForIndicators);
				row.put("acceptable start date", ReportColumnFormat.BOOLEANFORMAT,asd);
				LiteDate minEndDateForIndicators = (LiteDate.getOrCreate()).addMonths(-3);
				boolean aed  = records.getEndDate().after(minEndDateForIndicators);
				row.put("acceptable end date", ReportColumnFormat.BOOLEANFORMAT,aed);
				boolean asz  = records.getSize()>=240;
				row.put("acceptable size", ReportColumnFormat.BOOLEANFORMAT,asz);
//				needs 240 points
				row.put("acceptable for regression", ReportColumnFormat.BOOLEANFORMAT,(asd && asz && aed));
			} catch (Exception e) {
				System.err.println("Warning: failed to produce a report row, exception thrown.");
			}
		}
	}
}