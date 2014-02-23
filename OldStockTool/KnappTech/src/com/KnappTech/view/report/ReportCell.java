package com.KnappTech.view.report;

public class ReportCell<TYPE> {
	private final ReportRow<?> row;
	private final ReportColumn<TYPE> column;
	private final TYPE value;
	
	ReportCell(ReportRow<?> row,ReportColumn<TYPE> column, TYPE value) {
		if (row==null)
			throw new NullPointerException("null row.");
		if (column==null)
			throw new NullPointerException("null column.");
		if (value==null)
			throw new NullPointerException("null value.");
		this.row = row;
		this.column = column;
		this.value = value;
	}
	
	@Override
	public String toString() {
		String s = column.format(value);
		return column+"="+s;
	}
	
	public String value() {
		return column.format(value);
	}
	
	public TYPE getValue() {
		return value;
	}
	
	public ReportColumn<TYPE> getColumn() {
		return column;
	}
	
	public String getColumnName() {
		return column.getName();
	}
	
	public ReportRow<?> getRow() {
		return row;
	}
	
	public Object getRowObject() {
		return row.getObject();
	}
}