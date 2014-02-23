package com.KnappTech.sr.model.user;

//import java.io.IOException;
import java.util.ArrayList;

import com.KnappTech.model.KTObject;
import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.Asset;
import com.KnappTech.sr.model.comp.Currency;
import com.KnappTech.sr.model.constants.CurrencyType;
import com.KnappTech.sr.model.constants.InvestmentType;
import com.KnappTech.sr.model.constants.PositionUnits;
//import com.KnappTech.util.Filter;
//import com.KnappTech.view.report.ReportRow;

public class Position 
implements KTObject {
	private static final long serialVersionUID = 201001181202L;
	private final Asset asset;
	private final PositionUnits units;
	private final CurrencyType exchangeType; // by default.
	private final InvestmentType type;
	private boolean willingToTrade = false;
	private final ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	private boolean locked = false;
	private boolean permanentlyLocked = false;
	
	public Position(Asset asset,PositionUnits units,
			CurrencyType exchangeType,InvestmentType type) {
		this.asset = asset;
		this.units = units;
		this.exchangeType = exchangeType;
		this.type = type;
	}
	
	public Position(Asset asset) {
		this.asset = asset;
		this.units = PositionUnits.DOLLARS;
		this.exchangeType = CurrencyType.USDOLLAR;
		this.type = InvestmentType.STOCK;
	}
	
	public Position(Asset asset, double netWorth) {
		this(asset);
		easySetWorth(netWorth);
	}
	
	public Position(Asset asset, int quantity,double valueOfShare,boolean willingToTrade) {
		this(asset);
		easySetWorth(quantity,valueOfShare);
		this.setWillingToTrade(willingToTrade);
	}
	
	@Override
	public int compareTo(String o) {
		return 0;
	}

	public InvestmentType getType() {
		return type;
	}

	public PositionUnits getUnits() {
		return units;
	}

	public Asset getAsset() {
		return asset;
	}

	@Override
	public String getID() {
		return "";
	}

//	@Override
//	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
//		// TODO Auto-generated method stub
//	}
	
	@Override
	public boolean isValid() { return true; }

	public CurrencyType getExchangeType() {
		return exchangeType;
	}

	public Currency getCurrencyValue() {
		double currentQuantity = getCurrentQuantity(); // how many shares
		if (asset!=null) {
			Currency cr = asset.getValueOfShare();
			if (cr!=null) {
				double mostRecentValue = asset.getValueOfShare().getQuantity(); // value of a share
				Currency currency = new Currency(exchangeType);
				currency.setQuantity(currentQuantity*mostRecentValue);
				return currency;
			} else {
				System.err.println("Failed to get the value of share.");
			}
		} else {
			System.err.println("asset was null.");
		}
		return new Currency(CurrencyType.USDOLLAR);
	}

	public double getCurrentQuantity() {
		double currentQuantity = 0;
		for (Transaction trans : transactions) {
			currentQuantity+=trans.getQuantity();
		}
		return currentQuantity;
	}
	
	/**
	 * This is a shortcut to declaring how much you have.  This is useful
	 * because you don't have to go figuring out every transaction you ever
	 * made and reporting it.  The disadvantage to using this is some 
	 * functions like calculating your return on investment are not possible,
	 * that depends upon a real transaction history.  However, it makes it
	 * much easier to create an investor object and do other functions.
	 * @param q
	 */
	public void easySetWorth(double valueOfPosition) {
		if (canEdit()) {
			transactions.clear();
			int quantity = 1;
			double value = valueOfPosition;
			if (asset!=null) {
				Double lastPrice = asset.getValueOfShare().getQuantity();
				if (lastPrice!=null && lastPrice>0) {
					double qq = valueOfPosition/lastPrice;
					quantity = (int) Math.round(qq);
					value = lastPrice;
				}
			}
			Transaction t = Transaction.create(LiteDate.getOrCreate(), quantity, value, 0);
			transactions.add(t);
		}
	}
	
	public void easySetWorth(int quantity, double valueOfShare) {
		if (canEdit()) {
			transactions.clear();
			Transaction t = Transaction.create(LiteDate.getOrCreate(), quantity, valueOfShare, 0);
			transactions.add(t);
		}
	}

	public void setWillingToTrade(boolean willingToTrade) {
		this.willingToTrade = willingToTrade;
	}

	public boolean isWillingToTrade() {
		return willingToTrade;
	}
	
	public final synchronized boolean isLocked() {
		return locked;
	}
	
	public final synchronized boolean canEdit() {
		return !locked;
	}
	
	public final synchronized void lock() {
		locked = true;
	}
	
	public final synchronized void unlock() {
		if (!permanentlyLocked) {
			locked = false;
		}
	}
	
	public final synchronized void permanentlyLock() {
		permanentlyLocked = true;
		locked = true;
	}
}