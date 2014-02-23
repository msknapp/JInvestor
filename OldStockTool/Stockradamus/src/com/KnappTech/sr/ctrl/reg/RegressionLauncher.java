package com.KnappTech.sr.ctrl.reg;

import java.io.File;
import java.util.Collection;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.ctrl.PropertyManager;
import com.KnappTech.sr.model.Regressable.DefaultScorePersister;
import com.KnappTech.sr.model.Regressable.ERScoreKeeper;
import com.KnappTech.sr.model.Regressable.EconomicRecords;
import com.KnappTech.sr.persist.PersistenceRegister;

public class RegressionLauncher {
	// -XX:+UseParallelGC
	private static final ThreadPoolExecutor threadManager = 
		(ThreadPoolExecutor) Executors.newFixedThreadPool(6);
	
	public static void main(String[] args) {
//		logger.info("Started Regression Manager with args: "+args);
		String path = PropertyManager.getBaseDirectory();
		if (!path.endsWith(File.separator))
			path+=File.separator;
		path+="ERScore.xml";
		ERScoreKeeper.setPersister(new DefaultScorePersister(path));
		if (args!=null && args.length>0) {
			String s = args[0].toLowerCase();
			if (s.contains("portfolio")) {
				executeForPortfolio();
			} else if (s.contains("market") || s.contains("index") || s.contains("indices")){
				RegressionRuntimeSettings.setRunForWholeMarket(true);
				RegressionRuntimeSettings.setRunForLetters(false);
				execute();
			} else if (s.contains("letters")){
				RegressionRuntimeSettings.setRunForWholeMarket(false);
				RegressionRuntimeSettings.setRunForLetters(true);
				if (args.length>=3) {
					RegressionRuntimeSettings.setStartChar(args[1].toUpperCase().charAt(0));
					RegressionRuntimeSettings.setEndChar(args[2].toUpperCase().charAt(0));
				}
				execute();
			} else {
				boolean startSet = false;
				for (String arg : args) {
					if (arg.toLowerCase().startsWith("mincreationdate")) {
						String val = arg.substring(arg.indexOf("=")+1);
						String format = "yyyyMMdd";
						try {
							RegressionRuntimeSettings.setMinCreationDate(LiteDate.getOrCreate(val, format));
//							System.out.println("Minimum creation date set to "+minCreationDate);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (arg.toLowerCase().startsWith("minr2")) {
						// if it does not reach this minimum r2, it must be regressed.
						String val = arg.substring(arg.indexOf("=")+1);
						try {
							RegressionRuntimeSettings.setMinR2(Double.parseDouble(val));
//							System.out.println("Minimum R2 set to "+minR2);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (arg.toLowerCase().startsWith("termr2")) {
						// if the regression reaches this r2, it will move on.
						String val = arg.substring(arg.indexOf("=")+1);
						try {
							double et = Double.parseDouble(val);
							if (et<1)
								et = et*1000;
							int ets = (int) Math.round(et);
							RegressionRuntimeSettings.setEarlyTerminationScore(ets);
							System.out.println("The regresser will terminate early if score>"+
									RegressionRuntimeSettings.getEarlyTerminationScore());
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (arg.toLowerCase().startsWith("mintermr2")) {
						// if the regression reaches this r2, it will move on.
						String val = arg.substring(arg.indexOf("=")+1);
						try {
							double et = Double.parseDouble(val);
							if (et<1)
								et = et*1000;
							int ets = (int) Math.round(et);
							RegressionRuntimeSettings.setMinEarlyTerminationScore(ets);
							System.out.println("The regresser will not terminate early unless score>"+
									RegressionRuntimeSettings.getMinEarlyTerminationScore());
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (arg.toLowerCase().startsWith("market")) {
						RegressionRuntimeSettings.setRunForWholeMarket(true);
						RegressionRuntimeSettings.setRunForLetters(false);
					} else if (arg.toLowerCase().startsWith("der")) {
						String val = arg.substring(arg.indexOf("=")+1);
						Boolean b = Boolean.parseBoolean(val);
						RegressionRuntimeSettings.setConsiderDerivatives(b);
					} else if (arg.toLowerCase().startsWith("allrecords")) {
						String val = arg.substring(arg.indexOf("=")+1);
						Boolean b = Boolean.parseBoolean(val);
						RegressionRuntimeSettings.setConsiderAllRecords(b);
					} else {
						if (startSet) {
							if (arg.charAt(0)=='^') {
								RegressionRuntimeSettings.setEndChar('^');
								RegressionRuntimeSettings.setRunForLetters(false);
//								System.out.println("Set end character to "+endChar);
								continue;
							}
							RegressionRuntimeSettings.setEndChar(arg.toUpperCase().charAt(0));
//							System.out.println("Set end character to "+endChar);
						} else {
							if (arg.charAt(0)=='^') {
								RegressionRuntimeSettings.setStartChar('^');
								RegressionRuntimeSettings.setRunForWholeMarket(true);
								startSet=true;
								System.out.println("Set start character to ^");
								continue;
							}
							RegressionRuntimeSettings.setStartChar(arg.toUpperCase().charAt(0));
//							System.out.println("Set start character to "+startChar);
						}
					}
				}
				execute();
			}
		} else {
			execute();
		}
	}

	public static void execute() {
		ERInitializer.prepareEconomicRecords();
		int i = 65;
		if (RegressionRuntimeSettings.isRunForLetters()) {
			for (i=(int)RegressionRuntimeSettings.getStartChar();
					i<=(int)RegressionRuntimeSettings.getEndChar();i++) 
			{
				try {
					executeForLetter((char)i);
				} catch (Exception e) {
					System.err.println("Exception caught in fast regresser when trying " +
							"to execute for letter "+(char)i);
					e.printStackTrace();
				}
			}
		}
		try {
			if (RegressionRuntimeSettings.isRunForWholeMarket())
				executeForLetter('^');
		} catch (Exception e) {
			System.err.println("Exception caught in fast regresser when trying " +
					"to execute for letter ^");
			e.printStackTrace();
		}
		try {
			Thread.sleep(5000);
			while (threadManager.getActiveCount()>0)
				Thread.sleep(300000); // five minutes.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void executeForLetter(char letter) {
		CompaniesRegresser fr = new CompaniesRegresser(
				new TreeSet<String>(PersistenceRegister.company().getAllStoredIDs(letter)));
		threadManager.execute(fr);
	}
	
	public static void executeForPortfolio() {
		ERInitializer.prepareEconomicRecords();
		CompaniesRegresser fr = new CompaniesRegresser(
				new TreeSet<String>(PropertyManager.loadPortfolioTickers()));
		fr.run();
	}
}