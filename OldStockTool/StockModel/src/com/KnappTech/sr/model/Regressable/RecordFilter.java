package com.KnappTech.sr.model.Regressable;

public interface RecordFilter {
	public FilterResponse acceptable(RecordList<? extends Record> record);
	public RecordSet<? extends RecordList<? extends Record>> getCurrentRecords();
	public void setCurrentRecords(RecordSet<? extends RecordList<? extends Record>> records);
}
