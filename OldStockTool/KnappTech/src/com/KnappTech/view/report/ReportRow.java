package com.KnappTech.view.report;

import java.util.Hashtable;

public final class ReportRow<TYPE> {
	private static final long serialVersionUID = 201104152009L;
	private final Report<TYPE> report;
	private final TYPE object;
	private final Hashtable<ReportColumn<?>,ReportCell<?>> cells = 
		new Hashtable<ReportColumn<?>,ReportCell<?>>();
	private boolean include = true;

	ReportRow(Report<TYPE> report,TYPE object) {
		this.report = report;
		this.object = object;
	}
	
	public synchronized <T> ReportCell<T> getOrCreate(ReportColumn<T> column,T value) {
		if (value!=null) {
			ReportCell<T> cell = (ReportCell<T>) cells.get(column);
			if (cell==null) {
				cell = new ReportCell<T>(this,column,value);
				cells.put(column, cell);
			}
			return cell;
		}
		return null;
	}
	
	public synchronized <T> ReportCell<T> getOrCreate(String columnName,ReportColumnFormat<T> format,T value) {
		if (value!=null) {
			ReportColumn<T> column = report.getOrCreateColumn(columnName, format);
			return getOrCreate(column, value);
		}
		return null;
	}
	
	public synchronized <T> ReportCell<T> put(String columnName,ReportColumnFormat<T> format,T value) {
		if (value!=null) {
			ReportColumn<T> column = report.getOrCreateColumn(columnName, format);
			return getOrCreate(column, value);
		}
		return null;
	}
	
	public synchronized <T> ReportCell<T> getCell(ReportColumn<T> column) {
		if (column==null) 
			return null;
		ReportCell<T> cell = (ReportCell<T>) cells.get(column);
		return cell;
	}
	
	public synchronized ReportCell<?> getCell(String columnName) {
		ReportColumn<?> column = report.getColumn(columnName);
		if (column==null) 
			return null;
		ReportCell<?> cell = cells.get(column);
		return cell;
	}
	
	public synchronized <T> T getCellValue(ReportColumn<T> column) {
		ReportCell<T> cell = getCell(column);
		if (cell==null)
			return null;
		return cell.getValue();
	}
	
	public synchronized Object getCellValue(String columnName) {
		ReportCell<?> cell = getCell(columnName);
		if (cell==null)
			return null;
		return cell.getValue();
	}
	
	public synchronized <T> String getCellStringValue(ReportColumn<T> column) {
		ReportCell<T> cell = getCell(column);
		if (cell==null)
			return null;
		return cell.value();
	}
	
	public synchronized String getCellStringValue(String columnName) {
		ReportCell<?> cell = getCell(columnName);
		if (cell==null)
			return null;
		return cell.value();
	}

	@Override
	public String toString() {
		String s = "";
		for (ReportCell<?> cell : this.cells.values()) {
			s+=cell.toString()+",";
		}
		return s;
	}

	void setInclude(boolean include) {
		this.include = include;
	}

	boolean isInclude() {
		return include;
	}

	public TYPE getObject() {
		return object;
	}

	public synchronized ReportCell<?> get(ReportColumn<?> column) {
		return this.cells.get(column);
	}
}