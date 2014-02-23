package com.KnappTech.sr.model.comp;

//import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.math.linear.RealMatrix;

import com.KnappTech.model.KTObject;
import com.KnappTech.model.LiteDate;
//import com.KnappTech.model.ReportableSet;
//import com.KnappTech.sr.ctrl.opt.SimulatedPosition;
import com.KnappTech.sr.model.AbstractKTSet;
import com.KnappTech.sr.model.ThreadSafeRealMatrix;
import com.KnappTech.sr.model.Regressable.PriceHistory;
//import com.KnappTech.sr.persistence.CompanyManager;
import com.KnappTech.util.DataPoints;
//import com.KnappTech.util.Filter;
//import com.KnappTech.view.report.Report;
//import com.KnappTech.view.report.ReportRow;

public class Companies extends AbstractKTSet<Company> //implements ReportableSet 
{
	private static final long serialVersionUID = 201001161719L;
	
	public Companies() {
	}
	
	public Companies(Collection<Company> companies) {
		super(companies);
	}
	
	public char getLetter() {
		return items.iterator().next().getID().charAt(0);
	}

	@Override
	public int compareTo(String o) {
		return 0;
	}

	public LinkedHashSet<String> getTickerSet() {
		LinkedHashSet<String> tickers = new LinkedHashSet<String>();
		for (Company company : items) {
			tickers.add(company.getID());
		}
		return tickers;
	}
	
	@Override 
	public String toString() {
		String str = "";
		for (KTObject object : items) {
			str+=object.getID()+", ";
		}
		if (str.length()>100) str = str.substring(0,100);
		return str;
	}
	
	public Set<String> getIDs() {
		HashSet<String> ids = new HashSet<String>();
		for (Company company : items) {
			ids.add(company.getID());
		}
		return ids;
	}
	
	public RealMatrix produceCovarianceMatrix(boolean multiThread) {
		ThreadSafeRealMatrix matrix = new ThreadSafeRealMatrix(size());
		Collection<Thread> ts = new HashSet<Thread>();
		PriceHistory rowHistory = null;
		PriceHistory columnHistory = null;
		int row = 0;
		int column = 0;
		for (Company rowCompany : items) {
			rowHistory = rowCompany.getPriceHistory();
			for (Company columnCompany : items) {
				columnHistory = columnCompany.getPriceHistory();
				Runnable r = new Worker(rowHistory,columnHistory,row,column,matrix);
				if (multiThread) {
					Thread t = new Thread(r,"worker");
					ts.add(t);
					t.start();
				} else {
					r.run();
				}
				column++;
			}
			row++;
		}
		if (multiThread) {
			boolean threadIsRunning = true;
			try {
				while (threadIsRunning) {
					Thread.sleep(500);
					threadIsRunning = false;
					for (Thread t : ts) {
						if (t.isAlive()) {
							threadIsRunning = true;
							break;
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Finished producing covariance matrix.");
		return matrix.getMatrix();
	}
	
	private class Worker implements Runnable {
		private PriceHistory rowHistory = null;
		private PriceHistory columnHistory = null;
		private int row = 0;
		private int col = 0;
		private ThreadSafeRealMatrix matrix = null;
		
		public Worker(PriceHistory rowHistory, PriceHistory columnHistory, 
				int row, int col,ThreadSafeRealMatrix matrix) {
			this.row = row;
			this.col = col;
			this.rowHistory = rowHistory;
			this.columnHistory = columnHistory;
			this.matrix = matrix;
		}
		
		public void run() {
			DataPoints dp = columnHistory.getDataPoints(rowHistory,LiteDate.getOrCreate(Calendar.YEAR,-3));
			double cov = dp.getCovariance();
			matrix.setEntry(row, col, cov);
			if (col!=row) {
				matrix.setEntry(col, row, cov);
			}
		}
	}

//	@Override
//	public boolean save(boolean multiThread) throws InterruptedException {
//		boolean worked = true;
//		for (Company company : items) {
//			if (!CompanyManager.save(company, true)) {
//				worked = false;
//			}
//		}
//		return worked;
//	}

	public void clearRegressionResults() {
		for (Company company : this.items) {
			company.getRegressionResultsSet().clear();
			System.out.println("Cleared regression results for "+company.getID());
		}
	}

//	public void regress(RegressionManager regressionManager) {
//		for (Company company : data) {
//			regressionManager.regressCompany(company);
//		}
//	}
}