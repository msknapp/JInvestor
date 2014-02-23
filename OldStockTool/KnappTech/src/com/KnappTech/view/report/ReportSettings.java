package com.KnappTech.view.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
@Deprecated
public class ReportSettings {
	private static final long serialVersionUID = 201004181955L;
	private LinkedHashSet<String> columns = new LinkedHashSet<String>();
	private String sortBy = "id";
	private ArrayList<Object> bin = new ArrayList<Object>();
	public boolean ascending = true;
	public boolean includePriceHistory = true;
	public boolean includeFinancialHistory = true;
	public String sortRowsByAttributeName = "id";
	public boolean sortRowsByNumber = false;
	public String dateFormat = "yyyy/MM/dd";
	public String useRegressionThatIs = "most recent"; // or "most accurate"
	public boolean getEstimate = false;
	public double lastValue = 0;
	public boolean includeCompaniesWithoutRegression = false;
	public double volatility = 0;
	public double beta = 4;
	public double maxRows = -1;
	public boolean calculateScore = true;
	public boolean shortMode = false;
	
	public ReportSettings() {}
	
	public void addColumn(String columnHeading) {
		columns.add(columnHeading.toLowerCase());
	}
	
	public void addColumns(Collection<String> columnHeadings) {
		for (String columnHeading : columnHeadings) {
			columns.add(columnHeading.toLowerCase());
		}
	}
	
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
	
	public String getSortBy() {
		return sortBy;
	}
	
	public void setColumns(LinkedHashSet<String> columns) {
		columns.addAll(columns);
	}
	
	public LinkedHashSet<String> getColumns() {
		return columns;
	}

	public String getColumnsHTML() {
		String html = "<tr>";
		for (String heading : columns) {
			html += "<td class=\"header\" name=\""+heading+"\"><b>"+heading+"</b></td>";
		}
		html += "</tr>\n";
		return html;
	}

	public Object getColumnsCSV() {
		String csv = "";
		for (String heading : columns) {
			csv+=heading+",";
		}
		csv+="\n";
		return csv;
	}
	
	/**
	 * Stores the object in the bin.  Be sure to not
	 * store more than one object of a single type
	 * in the settings, or you won't be able to 
	 * retrieve one of them.
	 * @param o
	 */
	public void store(Object o) {
		bin.add(o);
	}
	
	/**
	 * Uses the class name you provide to find any object
	 * kept in the bin with that class name, and deliver
	 * it to the user.
	 * @param className
	 * @return
	 */
	public Object get(String className) {
		for (Object o : bin) {
			if (o.getClass().getName().equals(className)) {
				return o;
			}
		}
		return null;
	}
}