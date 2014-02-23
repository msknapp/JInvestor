package com.KnappTech.sr.ctrl.acq;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.sr.ctrl.PropertyManager;
import com.KnappTech.sr.ctrl.parse.FinancialParser;
import com.KnappTech.sr.model.Financial.FinancialEntryType;
import com.KnappTech.sr.model.Financial.FinancialHistories;
import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.comp.Companies;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.util.KTTimer;

public class FinancialRetriever {
	private static char startChar = 'A';
	private static char endChar = 'Z';
	private static int maxThreads = 3;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args==null || args.length<1) {
			runForMasses();
			return;
		}
		if (args[0].toLowerCase().contains("portfolio")) {
			runForPortfolio();
			return;
		}
		startChar=args[0].toUpperCase().charAt(0);
		if (args.length>=2)
			endChar=args[1].toUpperCase().charAt(0);
		runForMasses();
	}
	
	private static void runForPortfolio() {
		Collection<String> portfolioTickers = PropertyManager.loadPortfolioTickers();
		PersistenceRegister.company().clear();
		PersistenceRegister.financial().clear();
		Companies companies = null;
		try {
			companies = (Companies) PersistenceRegister.company().getAllThatAreStored(portfolioTickers, false);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		FinancialHistories financials = null;
		try {
			financials = (FinancialHistories) PersistenceRegister.financial().getAllThatAreStored(portfolioTickers, false);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("FYI: finished loading all companies and financials.");
		
		for (String name : companies.getIDs()) {
			if (!financials.has(name)) {
				FinancialHistory financial = FinancialHistory.create(name);
				financials.add(financial);
			}
		}
		updateAll(financials.getItems());
	}
	
	private static void updateAll(Collection<FinancialHistory> histories) {
		FinancialParser fp = new FinancialParser();
		Iterator<FinancialHistory> iter = histories.iterator();
		while (iter.hasNext()){
			try {
				FinancialHistory financial = iter.next();
				String msg = "taking too long to retrieve financial data for "+financial.getID();
				KTTimer escapeTimer = new KTTimer("Escape Financial Retrieval", 8, msg, true);
				System.out.println("Now working on financials for company: "
						+financial.getID());
				fp.update(financial);
				PersistenceRegister.financial().save(financial,true);
				PersistenceRegister.financialType().Save(false);
				escapeTimer.stop();
			} catch (Exception e) {
				System.err.println("Exception caught.");
			}
		}
	}
	
	private static void runForMasses() {
		System.out.println("Running financial retriever from "+startChar+" to "+endChar);
		try {
			PersistenceRegister.financialType().Load(false);
			IdentifiableList<FinancialEntryType, String> ls = 
				PersistenceRegister.financialType().getEverythingStored(false);
			for (FinancialEntryType fet : ls.getItems())
				if (FinancialEntryType.get(fet.getName())==null)
					System.out.println("Missing.");
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		int i = 0;
		for (i=(int)startChar;i<=(int)endChar;i++) {
			try {
				runForLetter((char)i);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Warning: exception caught while running " +
						"financial retriever for letter "+(char)i);
			}
		}
		System.out.println("Finished running for all letters!");
	}
	
	private static void runForLetter(char letter) {
		PersistenceRegister.company().clear();
		PersistenceRegister.financial().clear();
		Companies companies = null;
		try {
			companies = (Companies) PersistenceRegister.company().getEverythingStored(letter,false);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		FinancialHistories financials = null;
		try {
			financials = (FinancialHistories) PersistenceRegister.financial().getEverythingStored(letter,false);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("FYI: finished loading all companies and financials.");
		
		for (String name : companies.getIDs()) {
			if (!financials.has(name)) {
				FinancialHistory financial = FinancialHistory.create(name);
				financials.add(financial);
			}
		}
		update(financials.getItems(),maxThreads);
		System.out.println("Finished running for letter "+letter);
	}
	
	public static void update(Collection<FinancialHistory> histories,int maxThreads) {
		FinancialParser fp = new FinancialParser();
		ThreadPoolExecutor ex = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
		for (FinancialHistory financial : histories) {
			try {
				Runnable mr = new MyRunner(financial, fp);
				ex.execute(mr);
//				Thread t = new Thread(mr,"financial retriever");
//				long startTime = System.currentTimeMillis();
//				long elapsedTime = 0;
//				t.start();
//				Thread.sleep(100);
//				while (t.isAlive() && elapsedTime<10000) {
//					Thread.sleep(1000);
//					elapsedTime = System.currentTimeMillis()-startTime;
//				}
//				if (t.isAlive()) {
//					System.err.println("The current retriever thread is not finishing," +
//							" we are starting a new one and ignoring that one, it will " +
//							"most likely never finish.");
//					t.interrupt();
//				}
				// we move on even if the thread never wakes.
				// it might be running forever.
				// but I cannot afford to stop.
			} catch (Exception e) {
				System.err.println("Exception caught.");
			}
		}
		try {
			while (ex.getActiveCount()>0) {
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ex.shutdown();
	}
	
	public static class MyRunner implements Runnable {
		private FinancialHistory financial = null;
		private FinancialParser fp = null;
		
		public MyRunner(FinancialHistory financial,FinancialParser fp) {
			this.financial = financial;
			this.fp = fp;
		}

		@Override
		public void run() {
			try {
				String msg = "taking too long to retrieve financial data for "+financial.getID();
				KTTimer escapeTimer = new KTTimer("Escape Financial Retrieval", 30, msg, true);
				System.out.println("Now working on financials for company: "
						+financial.getID());
				fp.update(financial);
				escapeTimer.stop();
				PersistenceRegister.financial().save(financial,true);
				PersistenceRegister.financialType().saveAll(FinancialEntryType.createIDL(), false);
			} catch (Exception e) {
				System.err.println("Exception caught, stopping financial find for "+financial.getID()
						+", exception type: "+e.getClass()+", stack trace: ");
				e.printStackTrace();
			}
		}
	}
}