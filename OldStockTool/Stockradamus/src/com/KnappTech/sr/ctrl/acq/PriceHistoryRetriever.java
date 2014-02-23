package com.KnappTech.sr.ctrl.acq;

import java.util.Collection;

import com.KnappTech.sr.ctrl.parse.IPHParser;
import com.KnappTech.sr.ctrl.parse.PHParserDailyFinance;
import com.KnappTech.sr.ctrl.parse.PHParserGoogle;
import com.KnappTech.sr.ctrl.parse.PHParserYahoo;
import com.KnappTech.sr.model.Regressable.PriceHistories;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.beans.PriceHistoryStatusBean;
import com.KnappTech.sr.model.comp.Companies;
import com.KnappTech.sr.persist.PersistenceRegister;

public class PriceHistoryRetriever {
	private static char startChar = 'A';
	private static char endChar = 'Z';
	private static IPHParser parser = new PHParserYahoo();
	
	public static void main(String[] args) {
		if (args!=null && args.length>=1) {
			String arg1 = args[0];
			if (arg1.trim().toLowerCase().contains("market")) {
				runForLetter('^');
				return;
			}
			startChar = arg1.trim().toUpperCase().charAt(0);
			if (args.length>=2)
				endChar = args[1].trim().toUpperCase().charAt(0);
			if (args.length>=3) {
				if (args[2].equals("google")) 
					parser = new PHParserGoogle();
				else if (args[2].equals("yahoo"))
					parser = new PHParserYahoo();
				else if (args[2].equals("dailyfinance"))
					parser = new PHParserDailyFinance();
			}
		}
		int i = 65;
		for (i=(int)startChar;i<=(int)endChar;i++) {
			try {
				runForLetter((char)i);
			} catch (Exception e) {
				System.err.println("Unhandled exception thrown when " +
						"running price history retriever for "+(char)i);
			}
		}
	}
	
	private static void runForLetter(char letter) {
		try {
			PersistenceRegister.company().clear();
			PersistenceRegister.ph().clear();
			Companies companies = null;
			PriceHistories histories = null;
			try {
				companies = (Companies) PersistenceRegister.company().getEverythingStored(letter,false);
				histories = (PriceHistories) PersistenceRegister.ph().getEverythingStored(letter,false);
				System.out.println("FYI: finished loading all companies.");
			} catch (Exception e) {
				System.err.println("Price History Retriever failed to load companies " +
						"and price histories.");
			}
			for (String name : companies.getIDs()) {
				if (histories.has(name))
					continue;
				if (PersistenceRegister.ph().hasStored(name)) {
					System.out.println("Failed to load price history for "+name);
					
				}
				PriceHistory history = PriceHistory.create(name);
				histories.add(history);
			}
			update(histories.getItems());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("An unhandled exception caused letter "+letter+" to fail.");
		}
	}
	
	public static void update(Collection<PriceHistory> items) {
		String id = null;
		
		for (PriceHistory history : items) {
			try {
				System.out.println("Now working on company: "+history.getID());
				parser.setPriceHistory(history);
				parser.update();
				if (history.isValid()) {
					PersistenceRegister.ph().save(history,true);
					try { // should save the status too.
						PriceHistoryStatusBean bn = new PriceHistoryStatusBean(history);
						PersistenceRegister.phStatus().save(bn, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				id = history.getID();
				PersistenceRegister.company().remove(id);
				PersistenceRegister.ph().remove(id);
			} catch (Exception e) {
				String msg = "Exception caught while downloading price history";
				System.err.println(msg);
				e.printStackTrace();
			}
		}
	}
}