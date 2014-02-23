package com.KnappTech.sr.view.report;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.KnappTech.model.Reportable;
import com.KnappTech.sr.model.Regressable.ERScoreKeeper;
import com.KnappTech.sr.model.comp.Companies;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.comp.RegressionResult;
import com.KnappTech.sr.model.user.Investor;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.sr.reporter.CompaniesReporter;
import com.KnappTech.sr.reporter.ERScoresReporter;
import com.KnappTech.sr.reporter.RegressionResultReporter;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.Report;
import com.KnappTech.view.report.ReportRow;

public class ReportFactory {
	private static final char startChar = 'A';
	private static final char endChar = 'Z';
	private static final boolean runForLetters=true;
	private static boolean useMostRecentRegression = true;
	private static Investor investor = null;
	
	static {
		try {
			investor = PersistenceRegister.investor().getIfStored("MichaelKnapp", false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// decide on the reportableset class to use,
	// decide on the settings to use.
	// Then tell report builder those things
	public static void main(String[] args) {
		if (args==null || args.length<1) {
			produceReportOnMyPortfolio();
			produceIndustryReport();
			produceCompanyReport();
		} else {
			chooseAndRunReport(args);
		}
		System.exit(0);
	}
	
	public static void chooseAndRunReport(String[] args) {
		String arg = args[0];
		if (arg.contains("portfolio")) {
			produceReportOnMyPortfolio();
		} else if (arg.contains("market")) {
			produceReportOnIndicators();
		} else if (arg.contains("industry")) {
			produceIndustryReport();
		} else if (arg.contains("company")) {
			produceCompanyReport();
		} else if (arg.contains("indicators")) {
			produceReportOnIndicators();
		} else if (arg.contains("price")) {
			producePriceReport();
		} else if (arg.contains("financial")) {
			produceFinancialsReport();
		} else if (arg.contains("economic")) {
			produceEconomicReport();
		} else if (arg.contains("most_recent_regression")) {
			String ticker = args[1];
			produceMostRecentRegressionReport(ticker);
		} else if (arg.contains("most_accurate_regression")) {
			String ticker = args[1];
			produceMostAccurateRegressionReport(ticker);
		} else if (arg.contains("erscore")) {
			produceERScoreReport();
		} else {
			System.out.println("Could not understand what report you wanted produced.");
		}
	}
	
	public static void produceERScoreReport() {
		System.out.println("Going to produce er score report.");
		ERScoreKeeper.lazyLoad();
		Filter<Object> instructions = new Filter<Object>() {
			@Override
			public boolean include(Object object) {
				return true;
			}
		};
		ERScoreKeeper.lazyLoad();
		Report<?> report = new ERScoresReporter().getReport(instructions);
		String[] firstColumns = {"id","order","times used","number of entries",
				"start date","end date"};
		List<String> fcols = Arrays.asList(firstColumns);
		report.bringColumnsToFront(fcols);
		report.removeColumns(new String[] {"similar"});
		report.produceCSV(10000);
	}

	public static void produceMostAccurateRegressionReport(String ticker) {
		produceRegressionReport(ticker, false);
	}
	
	public static void produceMostRecentRegressionReport(String ticker) {
		produceRegressionReport(ticker, true);
	}
	
	public static void produceRegressionReport(String ticker,boolean mostRecent) {
		Filter<Object> instructions = new Filter<Object>() {
			@Override
			public boolean include(Object object) {
				return true;
			}
		};
		Company company = null;
		try {
			PersistenceRegister.erStatus().getEverythingStored(false);
			company = PersistenceRegister.company().getIfStored(ticker,false);
			company.setPriceHistory(PersistenceRegister.ph().getIfStored(ticker,false));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		RegressionResult rr = null;
		if (mostRecent)
			rr = company.getRegressionResultsSet().getMostRecent();
		else 
			rr = company.getRegressionResultsSet().getMostAccurate();
		Report<?> report = new RegressionResultReporter(rr).getReport(instructions);
		String[] firstColumns = {"id","indicator","derivative","factor","last value",
				"product","error","start date","end date","number of records",
				"volatility","update frequency"};
		List<String> fcols = Arrays.asList(firstColumns);
		report.bringColumnsToFront(fcols);
		report.produceCSV(200);
	}
	
	public static void produceIndustryReport() {
		produceReportOnIndicators();
	}
	
	public static void produceReportOnIndicators() {
		String[] fcols = {"id","score","last value","estimate",
				"probability of increase","z","r2","volatility","CR","roi",
				"standard deviation","beta","MC/TE","percent of value",
				"TE (M$)","ph exists","fh exists","in"};
		produceBasicLetteredReport("Companies", ItemGetter.COMPANY, fcols, "roi", 10000,true, true, true,'^','^');
	}
	
	public static void produceEconomicReport() {
		String[] fcols = {"id","number of records","start date","end date",
				"last update","last successful update",
				"source agency","update frequency","volatility"};
		produceBasicLetteredReport("Economic", ItemGetter.ECONOMIC, fcols, "roi", 10000,false, true, true);
	}
	
	public static void producePriceReport() {
		String[] fcols = {"id"};
		produceBasicLetteredReport("Price_Histories", ItemGetter.PRICE, fcols, "roi", 10000,false, true, true);
	}
	
	public static void produceCompanyReport() {
		String[] fcols = {"id","score","last value","estimate",
				"probability of increase","z","r2","volatility","CR","roi",
				"standard deviation","beta","MC/TE","percent of value",
				"TE (M$)","ph exists","fh exists","in"};
		produceBasicLetteredReport("Companies", ItemGetter.COMPANY, fcols, "roi", 10000,true, true, true);
	}
	
	public static void produceFinancialsReport() {
		String[] fcols = {"id"};
		produceBasicLetteredReport("Financial histories", ItemGetter.FINANCIAL, 
				fcols, "roi", 10000,false, true, true);
	}
	private static <T extends Reportable> void produceBasicLetteredReport(String reportName,
			ItemGetter<T> itemGetter,String[] firstColumns, String orderBy,int maxRows,boolean loadERs,
			boolean loadPHs,boolean loadFHs){
		produceBasicLetteredReport(reportName, itemGetter, firstColumns, orderBy, maxRows,
				loadERs, loadPHs, loadFHs, startChar, endChar);
	}
	
	private static <T extends Reportable> void produceBasicLetteredReport(String reportName,
			ItemGetter<T> itemGetter,String[] firstColumns, String orderBy,int maxRows,boolean loadERs,
			boolean loadPHs,boolean loadFHs,char startChar,char endChar){
		Report<T> report = new Report<T>(reportName);
		ReportRow<T> row;
		Filter<Object> instructions = new Filter<Object>() {
			@Override
			public boolean include(Object object) {
				return true;
			}
		};
		if (loadERs) {
			try {
				PersistenceRegister.erStatus().getEverythingStored(false);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		if (loadFHs)
			try {
				PersistenceRegister.financialType().getEverythingStored(false);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		if (runForLetters) {
			try {
				PersistenceRegister.sector().getEverythingStored(false);
				PersistenceRegister.industry().getEverythingStored(false);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			for (int i = (int)startChar;i<=(int)endChar;i++) {
				try {
					char letter = (char)i;
					System.out.println("Now working on report for letter "+letter);
					if (loadPHs) 
						PersistenceRegister.phStatus().getEverythingStored(letter,false);
					if (loadFHs) {
						PersistenceRegister.financial().getEverythingStored(letter,false);
					}
					Collection<T> items = itemGetter.getItems(letter);
					for (T reportable : items) {
						try {
							row = report.put(reportable);
							reportable.updateReportRow(instructions, row);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (firstColumns!=null && firstColumns.length>0) {
			List<String> fcols = Arrays.asList(firstColumns);
			report.bringColumnsToFront(fcols);
		}
		if (orderBy!=null && orderBy.length()>0)
			report.orderRows(orderBy,false);
		report.produceCSV(maxRows);
		
	}
	
	public static void produceReportOnMyPortfolio() {
		Collection<String> portfolioTickers = ReportProperties.loadPortfolioTickers();
		
		Companies companies=null;
		try {
			companies = (Companies) PersistenceRegister.company().getAllThatAreStored(portfolioTickers,false);
			PersistenceRegister.phStatus().getAllThatAreStored(portfolioTickers,false);
			PersistenceRegister.financial().getAllThatAreStored(portfolioTickers,false);
			PersistenceRegister.erStatus().getEverythingStored(false);
			PersistenceRegister.sector().getEverythingStored(false);
			PersistenceRegister.industry().getEverythingStored(false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Filter<Object> instructions = new Filter<Object>() {
			@Override
			public boolean include(Object object) {
				return true;
			}
		};
		Report<Company> report = new CompaniesReporter(companies).getReport(instructions);
		String[] firstColumns = {"id","score","last value","estimate",
				"probability of increase","z","r2","volatility","CR","roi",
				"standard deviation","beta","MC/TE","percent of value",
				"TE (M$)","ph exists","fh exists","in"};
		List<String> fcols = Arrays.asList(firstColumns);
		report.bringColumnsToFront(fcols);
		report.orderRows("score",false);
		report.produceCSV(10000);
	}
	
	public static final boolean useMostRecentRegression() {
		return useMostRecentRegression;
	}
	
	public static final Investor getInvestor() {
		return investor;
	}
}