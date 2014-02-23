package com.KnappTech.sr.ctrl;

import java.util.Calendar;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.Regressable.PriceHistories;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.util.KTTimer;

public class BetaUpdater {
	private static PriceHistory indicator = null;
	private static double varianceOfIndicator = -1;

	private static LiteDate minDate = LiteDate.getOrCreate(Calendar.YEAR, -3);
	
	public static void main(String[] args) {
		int s = (int)'A';
		int f = (int)'Z';
		if (args.length>=2) {
			s = (int)args[0].toUpperCase().charAt(0);
			f = (int)args[1].toUpperCase().charAt(0);
		}
		try {
			indicator = PersistenceRegister.ph().getIfStored("^GSPC",false);
			varianceOfIndicator = indicator.calculateVarianceOfReturn(minDate);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = s;i<=f;i++) {
			char currentChar = (char)i;
			runForChar(currentChar);
		}
	}
	
	private static void runForChar(char currentChar) {
		PersistenceRegister.ph().clear();
		PriceHistories priceHistories = null;
		try {
			System.out.println("Started letter "+currentChar+" loading companies.");
			priceHistories = (PriceHistories) PersistenceRegister.ph().getEverythingStored(currentChar,true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Finished loading companies, beta calculation will begin.");
		updateBetas(priceHistories,currentChar,indicator,varianceOfIndicator);
	}
	
	public static void updateBetas(PriceHistories priceHistories, char currentChar, 
			PriceHistory indicator, double varianceOfIndicator) {
		LiteDate minDate = LiteDate.getOrCreate(Calendar.YEAR, -2);
		for (PriceHistory priceHistory : priceHistories.getItems()) {
			if (priceHistory==null)
				continue; // skip it.
			String id = priceHistory.getID();
			if (id==null || id.length()<1 || id.charAt(0)!=currentChar) {
				System.err.println("Given a wrong company! company = "+
						id+", letter = "+currentChar);
				continue; // skip it.
			}
			KTTimer timer = null;
			try {
				System.out.println("Now working on "+priceHistory.getID());
				String msg = "Took too long to calculate beta for "+priceHistory.getID();
				timer = new KTTimer("beta stopper", 20, msg, true);
				double cov = priceHistory.calculateCovarianceOfReturn(indicator,minDate);
				double beta = cov/varianceOfIndicator;
				priceHistory.setBeta(beta);
				if (priceHistory.isValid()) {
					PersistenceRegister.ph().save(priceHistory,true);
				} else {
					System.err.println("Price history did not validate!");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				timer.stop();
				Thread.interrupted();
			}
		}
	}
}