package com.KnappTech.sr.reporter;

import com.KnappTech.model.Reportable;
import com.KnappTech.sr.model.Regressable.EconomicRecord;

public class EconomicRecordReporter extends RecordListReporter<EconomicRecord> implements Reportable {
	private final EconomicRecord record;
	
	public EconomicRecordReporter(EconomicRecord record) {
		super(record);
		this.record = record;
	}
}