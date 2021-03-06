package com.KnappTech.sr.ctrl;

import com.KnappTech.sr.ctrl.acq.BLSRetriever;
import com.KnappTech.sr.ctrl.acq.FRBRetriever;
import com.KnappTech.sr.ctrl.acq.PriceHistoryRetriever;
import com.KnappTech.sr.ctrl.acq.SLFRetriever;
import com.KnappTech.sr.ctrl.reg.RegressionLauncher;
import com.KnappTech.sr.view.report.ReportFactory;
import com.KnappTech.util.KTTimer;

/**
 * Manages to keep all of my data as up to date as possible.
 * Is responsible for keeping the application running constantly
 * until it is as up to date as possible.  Runs updates to 
 * price histories, all indicators, and runs the regressions
 * for all companies.
 * 
 * @author Michael Knapp
 */
public class ChielfOperationsOfficer {
	// use this run time argument for debugging:
	// -XX:+UseParallelGC
	
	private static final boolean runConstantly = false;
	private static final boolean produceReport = false;

	public static void main(String[] args) {
		if (runConstantly) {
			manageUpdatesConstantly();
		} else {
			manageUpdates();
		}
	}
	
	public static void manageUpdatesConstantly() {
		try { // constantly makes updates every 48 hours.
			while (true) {
				manageUpdates();
				Thread.sleep(172800000); // 48 hours.
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void manageUpdates() {
		boolean hasUpdatedPriceHistory = false;
		boolean hasUpdatedFRB = false;
		boolean hasUpdatedSLF = false;
		boolean hasUpdatedBLS = false;
		
		// Tries to update data.  If it ever fails, it will just try again later 
		// until it has succeeded with all of them.
		while (!hasUpdatedBLS || !hasUpdatedFRB || !hasUpdatedSLF || !hasUpdatedPriceHistory)
		{
			if (!hasUpdatedPriceHistory) {
				try {
					String msg = "updating price history took too long.";
					// 8 hour time limit.
					KTTimer escapeTimer = new KTTimer("ph escape timer",28800,msg,true);
						
					PriceHistoryRetriever.main(null);
					hasUpdatedPriceHistory=true;
					escapeTimer.stop();
				} catch (Exception e) {
					System.err.println("Unhandled exception thrown by price history retriever.");
				}
			}
			if (!hasUpdatedFRB) {
				try {
					String msg = "updating FRB took too long.";
					// 5 hour time limit.
					KTTimer escapeTimer = new KTTimer("frb escape timer",18000,msg,true);
					FRBRetriever.main(null);
					hasUpdatedFRB=true;
					escapeTimer.stop();
				} catch (Exception e) {
					System.err.println("Unhandled exception thrown by FRB retriever.");
					e.printStackTrace();
				}
			}
			if (!hasUpdatedSLF) {
				try {
					String msg = "updating SLF took too long.";
					// 5 hour time limit.
					KTTimer escapeTimer = new KTTimer("slf escape timer",18000,msg,true);
					SLFRetriever.main(null);
					hasUpdatedSLF=true;
					escapeTimer.stop();
				} catch (Exception e) {
					System.err.println("Unhandled exception thrown by SLF retriever.");
					e.printStackTrace();
				}
			}
			if (!hasUpdatedBLS) {
				try {
					String msg = "updating BLS took too long.";
					// 5 hour time limit.
					KTTimer escapeTimer = new KTTimer("bls escape timer",18000,msg,true);
					BLSRetriever.main(null);
					hasUpdatedBLS=true;
					escapeTimer.stop();
				} catch (Exception e) {
					System.err.println("Unhandled exception thrown by BLS retriever.");
					e.printStackTrace();
				}
			} 
		}
		try {
			RegressionLauncher.main(null);
		} catch (Exception e) {
			System.err.println("Unhandled exception thrown by fast regresser.");
			e.printStackTrace();
		}
		System.out.println("Finished managing updates.");
		if (produceReport) 
		{	// must output a report.
			ReportFactory.produceIndustryReport();
			ReportFactory.produceCompanyReport();
		}
	}
}
