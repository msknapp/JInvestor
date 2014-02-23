package com.KnappTech.sr.reporter;

import com.KnappTech.model.Reportable;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.comp.Indicator;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.ReportColumnFormat;
import com.KnappTech.view.report.ReportRow;

public class IndicatorReporter implements Reportable {
	private final Indicator indicator;
	
	public IndicatorReporter(Indicator indicator) {
		this.indicator = indicator;
	}

	@Override
	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		try {
			row.put("indicator",ReportColumnFormat.ID,indicator.getID());
			row.put("factor",ReportColumnFormat.NINEDECIMAL,indicator.getFactor());
			row.put("error",ReportColumnFormat.USCURRENCY,indicator.getError());
			row.put("last value",ReportColumnFormat.USCURRENCY,indicator.getLastValue());
			row.put("type", ReportColumnFormat.INTFORMAT,(int) indicator.getType());
			row.put("derivative", ReportColumnFormat.BOOLEANFORMAT,indicator.isDerivative());
			row.put("product", ReportColumnFormat.THREEDECIMAL,indicator.getProduct());
			EconomicRecord record = PersistenceRegister.er().getIfLoaded(indicator.getID());
			if (record==null)
				return;
			new EconomicRecordReporter(record).updateReportRow(instructions, row);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
