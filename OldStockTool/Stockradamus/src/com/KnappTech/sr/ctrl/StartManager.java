package com.KnappTech.sr.ctrl;

import com.KnappTech.sr.ctrl.acq.BLSRetriever;
import com.KnappTech.sr.ctrl.acq.FRBRetriever;
import com.KnappTech.sr.ctrl.acq.FinancialRetriever;
import com.KnappTech.sr.ctrl.acq.PriceHistoryRetriever;
import com.KnappTech.sr.ctrl.acq.SLFRetriever;
import com.KnappTech.sr.ctrl.reg.ERScoreUpdater;
import com.KnappTech.sr.ctrl.reg.RegressionLauncher;
import com.KnappTech.sr.view.MainDisplay;
import com.KnappTech.sr.view.report.ReportFactory;

/**
 * This class will always be the default starter for the 
 * application.
 * @author Michael Knapp
 */
public class StartManager {
	
	
	/**
	 * Starts the entire application and lends the user options.
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length<1) {
			MainDisplay.start();
		}
		String s = args[0].toLowerCase();
		if (s.equals("x")) {
			MainDisplay.start();
		} else if (s.equals("update")) {
			if (args.length<2) {
				// update everything
				System.out.println("Going to update price histories, all financial data, " +
						"then all economic records.");
				FRBRetriever.main(null);
				SLFRetriever.main(null);
				BLSRetriever.main(null);
				PriceHistoryRetriever.main(null);
				FinancialRetriever.main(null);
			}
			String u = args[1].toLowerCase();
			if (u.contains("price")) {
				System.out.println("Going to update price histories.");
				String[] subArgs = new String[args.length-2];
				System.arraycopy(args, 2, subArgs, 0, args.length-2);
				PriceHistoryRetriever.main(subArgs);
			} else if (u.contains("financ")) {
				System.out.println("Going to update financial histories.");
				String[] subArgs = new String[args.length-2];
				System.arraycopy(args, 2, subArgs, 0, args.length-2);
				FinancialRetriever.main(subArgs);
			} else if (u.contains("economic")) {
				if (args.length<3) {
					System.out.println("Going to update economic records.");
					FRBRetriever.main(null);
					SLFRetriever.main(null);
					BLSRetriever.main(null);
				}
				String uu = args[2].toLowerCase();
				String[] subArgs = new String[args.length-3];
				System.arraycopy(args, 3, subArgs, 0, args.length-3);
				if (uu.equals("slf")) {
					System.out.println("Going to update SLF economic records.");
					SLFRetriever.main(subArgs);
				} else if (uu.equals("frb")) {
					System.out.println("Going to update FRB economic records.");
					FRBRetriever.main(subArgs);
				} else if (uu.equals("bls")) {
					System.out.println("Going to update BLS economic records.");
					BLSRetriever.main(subArgs);
				} else {
					System.out.println("Could not determine what economic records you wanted to update.");
				}
			} else if (u.contains("erscore")) {
				System.out.println("Going to update er score.");
				String[] subArgs = new String[args.length-2];
				System.arraycopy(args, 2, subArgs, 0, args.length-2);
				ERScoreUpdater.main(subArgs);
			} else if (u.contains("status")) {
				System.out.println("Going to update er and ph status.");
				String[] subArgs = new String[args.length-2];
				System.arraycopy(args, 2, subArgs, 0, args.length-2);
				StatusUpdater.main(subArgs);
			} else {
				System.out.println("Could not determine what you want to update.");
			}
			System.exit(0);
		} else if (s.equals("regress")) {
			String[] subArgs = new String[args.length-1];
			System.arraycopy(args, 1, subArgs, 0, args.length-1);
			System.out.println("Going to perform regressions.");
			RegressionLauncher.main(subArgs);
			System.exit(0);
		} else if (s.equals("report")) {
			String[] subArgs = new String[args.length-1];
			System.arraycopy(args, 1, subArgs, 0, args.length-1);
			System.out.println("Going to produce report(s).");
			ReportFactory.main(subArgs);
			System.exit(0);
		} else if (s.equals("beta")) {
			String[] subArgs = new String[args.length-1];
			System.arraycopy(args, 1, subArgs, 0, args.length-1);
			System.out.println("Going to calculate betas.");
			BetaUpdater.main(subArgs);
			System.exit(0);
//		} else if (s.contains("regressionservice") || s.contains("regressionserver")) {
//			String[] subArgs = new String[args.length-1];
//			System.arraycopy(args, 1, subArgs, 0, args.length-1);
//			System.out.println("Going to calculate betas.");
//			RemoteRegressionService.main(subArgs);
		} else if (s.contains("interdependence")) {
			String[] subArgs = new String[args.length-1];
			System.arraycopy(args, 1, subArgs, 0, args.length-1);
			System.out.println("Going to calculate interdependence.");
			InterDependenceCalculator.main(subArgs);
		} else if (s.equals("props")) {
			PropertyManager.printProperties();
		} else if (s.equals("prep")) {
			System.out.println("Going through the prep routine, update your portfolio " +
					"tickers file now if you have not already done so.");
			FRBRetriever.main(null);
			BLSRetriever.main(null);
			SLFRetriever.main(null);
			ReportFactory.produceERScoreReport();
			PriceHistoryRetriever.main(new String[] {"^","^"});
			ReportFactory.produceIndustryReport();
			FinancialRetriever.main(null);
			ReportFactory.produceFinancialsReport();
			PriceHistoryRetriever.main(new String[] {"A","Z"});
			ReportFactory.produceReportOnMyPortfolio();
			ReportFactory.producePriceReport();
			ReportFactory.produceCompanyReport();
		} else {
			if (!s.contains("help"))
				System.out.println("Could not understand your request.");
			String manual = "Example Stockradamus functions:\n"+
			"\tjava -jar Stockradamus.jar - will open these help instructions\n"+
			"\tjava -jar Stockradamus.jar x - will open the main display\n"+
			"\tjava -jar Stockradamus.jar update - will update price histories, financial data, and economic records\n"+
			"\tjava -jar Stockradamus.jar update price - will update price histories\n"+
			"\tjava -jar Stockradamus.jar update financial - will update financial data\n"+
			"\tjava -jar Stockradamus.jar update economic - will update economic records\n"+
			"\tjava -jar Stockradamus.jar update economic frb/bls/slf - will update either frb, bls, or slf data\n"+
			"\tjava -jar Stockradamus.jar update economic bls c f - will update slf records from c to f\n"+
			"\tjava -jar Stockradamus.jar regress - will update all regressions as necessary\n"+
			"\tjava -jar Stockradamus.jar regress b e - will update regressions for companies from B to E\n"+
			"\tjava -jar Stockradamus.jar update economic bls c f - will update slf records from c to f\n"+
			"\tjava -jar Stockradamus.jar update economic bls c f - will update slf records from c to f\n";
			System.out.println(manual);
			System.exit(0);
		}
		System.out.println("The application has finished and is exiting.");
		System.exit(0);
	}
}