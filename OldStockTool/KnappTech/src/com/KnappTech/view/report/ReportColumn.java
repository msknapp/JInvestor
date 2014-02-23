package com.KnappTech.view.report;

public class ReportColumn<TYPE> {
	
	private final String name;
	private final ReportColumnFormat<TYPE> format;
	private boolean include = true;
	
	ReportColumn(String name,ReportColumnFormat<TYPE> format) {
		this.name = name;
		this.format = format;
	}

	public String getName() {
		return name;
	}
	
	public String format(TYPE value) {
		return format.format(value);
	}

	void setInclude(boolean include) {
		this.include = include;
	}

	boolean isInclude() {
		return include;
	}

	public ReportColumnFormat<TYPE> getFormat() {
		return format;
	}
	
	public String toString() {
		return name;
	}
}