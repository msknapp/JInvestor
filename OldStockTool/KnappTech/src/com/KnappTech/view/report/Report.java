package com.KnappTech.view.report;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.KnappTech.model.LiteDate;
import com.KnappTech.util.FileUtil;
import com.KnappTech.util.Filter;

public final class Report<TYPE> {
	private static final long serialVersionUID = 201003252019L;
	private static File reportDirectory = null;
	private static SimpleDateFormat dateFormatter = null;
	
	private final String reportName;
	private final Hashtable<TYPE,ReportRow<TYPE>> rows = 
		new Hashtable<TYPE,ReportRow<TYPE>>();
	private final Hashtable<String,ReportColumn<?>> columns = 
		new Hashtable<String,ReportColumn<?>>();
	
	private transient Hashtable<Integer,ReportRow<TYPE>> orderedRows = null;
	private transient Hashtable<Integer,ReportColumn<?>> orderedColumns = null;
	
	public Report(String reportName) {
		if (reportName==null)
			throw new NullPointerException("Null report name.");
		if (reportName.length()<1)
			throw new IllegalArgumentException("Report name cannot be empty.");
		this.reportName = reportName;
	}
	
	public synchronized final <T> ReportColumn<T> getOrCreateColumn(
			String columnName, ReportColumnFormat<T> format) {
		try {
			ReportColumn<T> c = (ReportColumn<T>) columns.get(columnName);
			if (c==null) {
				c = new ReportColumn<T>(columnName, format);
				columns.put(columnName, c);
			}
			return c;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("A column already exists with "+
					"that name but a different format.",e);
		}
	}
	
	public synchronized final <T> ReportRow<TYPE> put(TYPE object) {
		ReportRow<TYPE> row = rows.get(object);
		if (row==null) {
			row = new ReportRow<TYPE>(this,object);
			rows.put(object, row);
		}
		return row;
	}
	
	public synchronized final <T> ReportCell<T> put(TYPE object,
			ReportColumn<T> column,T value) {
		return put(object).getOrCreate(column, value);
	}
	
	public synchronized final String toCSV(int maxRows) {
		if (maxRows<0)
			throw new IllegalArgumentException("cannot limit to a" +
					" negative number of rows.");
		StringBuilder sb = new StringBuilder();
		Hashtable<Integer,ReportColumn<?>> orderedColumns = getOrderedColumns();
		ReportColumn<?> column;
		for (int i =0;i<orderedColumns.size();i++) {
			column=orderedColumns.get(i);
			if (column.isInclude()) 
				sb.append(column.getName()+",");
		}
		sb.append("\n");
		Hashtable<Integer,ReportRow<TYPE>> orderedRows = getOrderedRows();
		String v;
		for (int i = 0;i<Math.min(maxRows,orderedRows.size());i++) {
			ReportRow<TYPE> row = orderedRows.get(i);
			for (int j = 0;j<orderedColumns.size();j++) {
				column = orderedColumns.get(j);
				if (column.isInclude()) {
					ReportCell<?> cl = row.get(column);
					v = (cl!=null ? cl.value() : "");
					v = v.replace(",", ";");
					sb.append(v+",");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public synchronized final void orderRows(Comparator<ReportRow<?>>
		comparator) {
		List<ReportRow<TYPE>> ls = new ArrayList<ReportRow<TYPE>>(rows.values());
		Collections.sort(ls,comparator);
		int i = 0;
		this.orderedRows = new Hashtable<Integer,ReportRow<TYPE>>();
		for (ReportRow<TYPE> row : ls) {
			orderedRows.put(i, row);
			i++;
		}
	}
	
	public synchronized final void orderRows(String fieldName,boolean ascending) {
		orderRows(createComparator(fieldName, ascending));
	}
	
	public synchronized final void resetRowFilter() {
		for (ReportRow<?> row : rows.values()) {
			row.setInclude(true);
		}
	}
	
	public synchronized final void filterRows(Filter<ReportRow<?>> filter) {
		List<ReportRow<TYPE>> rs = new ArrayList<ReportRow<TYPE>>
			(rows.values());
		Iterator<ReportRow<TYPE>> iter = rs.iterator();
		while (iter.hasNext()) {
			ReportRow<TYPE> o = iter.next();
			if (!filter.include(o))
				o.setInclude(false);
		}
	}
	
	public synchronized final void filterRows(String fieldName,double limit,boolean max) {
		filterRows(createFilter(fieldName,limit,max));
	}
	
	public static final Filter<ReportRow<?>> createFilter(String fieldName,double limit,boolean max) {
		return new MyFilter(fieldName,limit,max);
	}
	
	private static final class MyFilter implements Filter<ReportRow<?>> {
		private final String fieldName;
		private final double limit;
		private final boolean max;
		public MyFilter(String fieldName,double limit,boolean max) {
			this.fieldName = fieldName;
			this.limit = limit;
			this.max = max;
		}

		@Override
		public boolean include(ReportRow<?> object) {
			if (object==null)
				return false;
			Object o = object.getCellValue(fieldName);
			if (o == null)
				return false;
			if (o instanceof Number) {
				return (max ? ((Number)o).doubleValue()<limit : ((Number)o).doubleValue()>limit);
			} else {
				return (max ? o.hashCode()<limit : o.hashCode()>limit);
			}
		}
	}
	
	public synchronized final void setColumns(List<String> columns) {
		for (String columnName : this.columns.keySet()) {
			ReportColumn<?> column = this.columns.get(columnName);
			column.setInclude(columns.contains(columnName));
		}
		int i = 0;
		this.orderedColumns = new Hashtable<Integer,ReportColumn<?>>(
				this.columns.size());
		for (String col : columns) {
			orderedColumns.put(i, this.columns.get(col));
			i++;
		}
	}
	
	public synchronized final void bringColumnsToFront(List<String> columns) {
		Hashtable<Integer, ReportColumn<?>> newCols = new Hashtable<Integer, ReportColumn<?>>();
		int i=0;
		ReportColumn<?> col;
		for (String column : columns) {
			col = this.columns.get(column);
			if (col!=null && !newCols.values().contains(col)) {
				newCols.put(i, col);
				i++;
			}
		}
		Hashtable<Integer, ReportColumn<?>> oc = getOrderedColumns();
		for (int j = 0;j<oc.size();j++) {
			col = oc.get(j);
			if (col!=null && !newCols.values().contains(col)) {
				newCols.put(i,col);
				i++;
			}
		}
		this.orderedColumns = newCols;
	}
	
	public ReportColumn<?> getColumn(String columnName) {
		return columns.get(columnName);
	}
	
	private Hashtable<Integer,ReportRow<TYPE>> getOrderedRows() {
		if (orderedRows!=null)
			return orderedRows;
		orderedRows = new Hashtable<Integer,ReportRow<TYPE>>(rows.size());
		int i = 0;
		for (TYPE o : rows.keySet()) {
			orderedRows.put(i, rows.get(o));
			i++;
		}
		return orderedRows;
	}
	
	private Hashtable<Integer,ReportColumn<?>> getOrderedColumns() {
		if (orderedColumns!=null)
			return orderedColumns;
		orderedColumns = new Hashtable<Integer,ReportColumn<?>>(rows.size());
		int i = 0;
		for (String o : columns.keySet()) {
			if (columns.get(o)!=null) {
				orderedColumns.put(i, columns.get(o));
				i++;
			}
		}
		return orderedColumns;
	}
	
	@Override
	public String toString() {
		return reportName;
	}
	
	public void produceCSV(int maxRows) {
		String csvText = toCSV(maxRows);
		String dateFolderString = "";
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		dateFolderString = sdf2.format(Calendar.getInstance().getTime());
		String dateString = getTimeStamp();
		String fullPath = getReportDirectory()+File.separator+dateFolderString+
			File.separator+reportName+"_"+dateString+".csv";
		FileUtil.writeStringToFile(fullPath, csvText,true);
		System.out.println("Wrote report to file: "+fullPath);
	}
	
	private String getTimeStamp() {
		return getDateFormatter().format(new Date());
	}

	public String getReportName() {
		return reportName;
	}
	
	public static Comparator<ReportRow<?>> createComparator(String fieldName,boolean ascending) {
		return new MyComparator(fieldName,ascending);
	}
	
	private static class MyComparator implements Comparator<ReportRow<?>> {
		private final String fieldName;
		private boolean ascending;
		public MyComparator(String fieldName,boolean ascending) {
			this.fieldName = fieldName;
			this.ascending = ascending;
		}
		@Override
		public int compare(ReportRow<?> arg0, ReportRow<?> arg1) {
			if (arg0==null)
				return 1;
			if (arg1==null)
				return -1;
			try {
				Object o0 = arg0.getCellValue(fieldName);
				Object o1 = arg1.getCellValue(fieldName);
				if (o0==null)
					return 1;
				if (o1==null)
					return -1;
				double v0=0;
				double v1 = 0;
				if (o0 instanceof Number) {
					v0 = ((Number)o0).doubleValue();
					v1 = ((Number)o1).doubleValue();
				} else if (o0 instanceof LiteDate) {
					v0 = ((LiteDate)o0).hashCode();
					v1 = ((LiteDate)o1).hashCode();
				} else {
					int i = o0.toString().compareTo(o1.toString());
					return (ascending ? i : -1*i);
				}
				double d = v0-v1;
				int i = (int)Math.round(d);
				if (d>0 && i==0)
					d=1;
				if (d<0 && i==0)
					d=-1;
				return (ascending ? i : -1*i);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
	}
	
	private static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.contains("windows"));
	}

	public static void setReportDirectory(String reportDirectory) {
		Report.reportDirectory = new File(reportDirectory);
	}

	public static void setReportDirectory(File reportDirectory) {
		Report.reportDirectory = reportDirectory;
	}

	public static File getReportDirectory() {
		if (reportDirectory==null) {
			try {
				File f = new File( (isWindows()) ? "C:\\Reports\\" : "/Reports/");
				if (!f.exists()) {
					f.mkdir();
				}
				reportDirectory=f;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return reportDirectory;
	}

	public static void setDateFormatter(String dateFormatter) {
		Report.dateFormatter = new SimpleDateFormat(dateFormatter);
	}

	public static void setDateFormatter(SimpleDateFormat dateFormatter) {
		Report.dateFormatter = dateFormatter;
	}

	public static SimpleDateFormat getDateFormatter() {
		if (dateFormatter==null) {
			try {
				dateFormatter = new SimpleDateFormat("yyyyMMdd_kkmmss");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dateFormatter;
	}
	
	public Set<ReportColumn<?>> getColumns() {
		return Collections.unmodifiableSet(new HashSet<ReportColumn<?>>(columns.values()));
	}
	
	public List<ReportRow<TYPE>> getRows() {
		return Collections.unmodifiableList(new ArrayList<ReportRow<TYPE>>(rows.values()));
	}

	public void removeColumns(String[] columnNames) {
		for (String columnName : columnNames) {
			ReportColumn<?> col = getColumn(columnName);
			if (col==null)
				continue;
			col.setInclude(false);
		}
	}
}