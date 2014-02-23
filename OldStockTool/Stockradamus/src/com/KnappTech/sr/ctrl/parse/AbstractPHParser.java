package com.KnappTech.sr.ctrl.parse;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.util.MethodResponse;

public abstract class AbstractPHParser implements IPHParser {
	protected PriceHistory priceHistory = null;
	protected static final LiteDate lastTradeDate = LiteDate.getMostRecentTradingDay(LiteDate.getOrCreate());
	
	public PriceHistory getPriceHistory() {
		return priceHistory;
	}

	public void setPriceHistory(PriceHistory priceHistory) {
		this.priceHistory = priceHistory;
	}

	public MethodResponse update() throws InterruptedException {
		try {
			if (!needsUpdate())
				return new MethodResponse(false,null,null,"The price history is already " +
					"up to date, and needs no update.");
			LiteDate initialEndDate = priceHistory.getEndDate();
			String sourceURL = createURL();
			priceHistory.setLastAttemptedUpdate(LiteDate.getOrCreate());
			String data = Generic.retrieveWebPage(sourceURL, "GET");
			updatePH(data);
			if ((initialEndDate==null  && priceHistory.getEndDate()!=null) || 
				(initialEndDate!=null && priceHistory.getEndDate().after(initialEndDate))) {
				priceHistory.setLastSuccessfulUpdate(LiteDate.getOrCreate());
				System.out.println("Successfully updated price history for: "+priceHistory.getID());
			} else
				System.err.println("==> The update for "+priceHistory.getID()+" failed."+
						" end date is: "+priceHistory.getEndDate());
			return new MethodResponse(true,priceHistory,null,"update was successful.");
		} catch (InterruptedException e) {
			throw (InterruptedException)e;
		} catch (FileNotFoundException e) {
			System.err.println("Cannot update "+priceHistory.getID());
			return new MethodResponse(false,e,null,"File not found exception.");
		} catch (IOException e) {
			e.printStackTrace();
			String msg = "Exception caught while updating a price history: "+e.getMessage() + "\nclass: "+e.getClass();
			System.err.println(msg);
			return new MethodResponse(false,e,null,"IO exception.");
		} catch (Exception e) {
			if (e instanceof InterruptedException)
				throw (InterruptedException)e;
			e.printStackTrace();
			String msg = "Exception caught while updating a price history: "+e.getMessage() + "\nclass: "+e.getClass();
			System.err.println(msg);
			return new MethodResponse(false,e,null,"Unhandled exception.");
		}
	}
	
	public boolean needsUpdate() {
		if (priceHistory.isEmpty() || priceHistory.getLastDate().before(lastTradeDate))
			return true;
		return false;
	}
	
	public abstract String createURL();
	
	public abstract void updatePH(String data) throws InterruptedException;
}