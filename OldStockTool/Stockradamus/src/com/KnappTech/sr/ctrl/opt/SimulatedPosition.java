package com.KnappTech.sr.ctrl.opt;

import java.text.NumberFormat;

import com.KnappTech.sr.ctrl.parse.PHParserYahoo;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.comp.RegressionResult;
import com.KnappTech.sr.model.user.Investor;
import com.KnappTech.sr.persist.PersistenceRegister;

public class SimulatedPosition {
	private Company company = null;
	private double priceOfShare = 0;
	private int numberOfShares = 0;
	
	private double estimatedFinalPriceOfShare = 0;
	
	private SimulatedPosition() {
		
	}
	
	public SimulatedPosition(Company company) {
		this.setCompany(company);
		PriceHistory ph = company.getPriceHistory();
		try {
			update(ph);
		} catch (Exception e) {
			e.printStackTrace();
		}
		priceOfShare = ph.getLastValue();//company.getLastKnownPrice();
		RegressionResult rs = company.getRegressionResultsSet().getMostAccurate();
		setEstimatedFinalPriceOfShare(rs.getUpToDateEstimate());
	}
	
	public void update(PriceHistory ph) throws InterruptedException {
		PHParserYahoo php = new PHParserYahoo();
		
		php.setPriceHistory(ph);
		php.update();
		try {
			PersistenceRegister.ph().save(ph,true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public SimulatedPosition spawnChildPosition(Investor investor,double maxOffset) {
		SimulatedPosition input = clone();
		input.jumpQuantities(investor,maxOffset);
		return input;
	}
	
	public void jumpQuantities(Investor investor,double maxOffset) {
		if (investor.isWillingToTrade(company)) {
			double randomNumber = 0;
			int initialNumberOfShares = investor.getNumberOfShares(company);
			int newNumberOfShares=0;
			double newPositionValue = 0;
			double currentPositionValue = getPositionValue();
			double maxInvestment = investor.getMaxAllowedInvestmentInSingleStock();
			double minInvestment = 0;
			if (investor.isWillingToShort()) {
				minInvestment=-maxInvestment;
			}
			if (getEstimatedFinalPriceOfShare()>getPriceOfShare())
			{	// expecting an increase in value.
				minInvestment=Math.min(0, initialNumberOfShares);
				// meaning you are not allowed to short it more than 
				// you may have already shorted it.
			} else
			{	// expecting a decrease in value.
				maxInvestment=Math.max(0, initialNumberOfShares);
				// meaning you are not allowed to finish with more shares
				// than you may already have.
			}
			
			randomNumber = Math.random();
			newPositionValue = currentPositionValue+2*(randomNumber-0.5)*maxOffset;
			newNumberOfShares = (int)(Math.round(newPositionValue/priceOfShare));
			newPositionValue = newNumberOfShares*priceOfShare;
			if (newPositionValue+1>maxInvestment) {
				newPositionValue=maxInvestment;
				newNumberOfShares = (int)(Math.floor(newPositionValue/priceOfShare));
			} else if (newPositionValue-1<minInvestment) {
				newPositionValue=minInvestment;
				newNumberOfShares = (int)(Math.ceil(newPositionValue/priceOfShare));
			} else if (Math.abs(newNumberOfShares-initialNumberOfShares)<2)
			{	// a shortcut might speed things up a little bit.
				newNumberOfShares=initialNumberOfShares;
			}
			setNumberOfShares(newNumberOfShares);
		} else {
			setNumberOfShares(investor.getNumberOfShares(company));
		}
	}
	
	public double getPositionValue() {
		return numberOfShares*priceOfShare;
	}
	
	public double getEstimatedFinalPositionValue() {
		if (estimatedFinalPriceOfShare<0) {
			estimatedFinalPriceOfShare=0;
		}
		return numberOfShares*estimatedFinalPriceOfShare;
	}
	
	public double getTransactionCost(double fee) {
		return getTransactionCost(fee,0);
	}
	
	public double getTransactionCost(double fee,Investor investor) {
		int initialNumberOfShares = investor.getNumberOfShares(company);
		return getTransactionCost(fee,initialNumberOfShares);
	}
	
	public double getTransactionCost(double fee,int initialNumberOfShares) {
		int sharesBought = numberOfShares-initialNumberOfShares;
		double d = fee;
		if (Math.abs(sharesBought)==0) {
			return 0;
		}
		return sharesBought*priceOfShare+d;
	}
	
	public SimulatedPosition clone() {
		SimulatedPosition cl = new SimulatedPosition();
		cl.setNumberOfShares(numberOfShares);
		cl.setEstimatedFinalPriceOfShare(getEstimatedFinalPriceOfShare());
		cl.setPriceOfShare(priceOfShare);
		cl.setCompany(getCompany());
		return cl;
	}

	public String getDetails(Investor investor) {
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		int initialNumberOfShares = investor.getNumberOfShares(getCompany());
		int finalNumberOfShares = numberOfShares;
		int buy = finalNumberOfShares-initialNumberOfShares;
		if (buy!=0) {
			String ticker = getCompanyTicker();
			String str = ticker+"; "+initialNumberOfShares+"; "+buy+
				"; "+finalNumberOfShares+"; "+
				nf.format(priceOfShare)+"; "+
				nf.format(getPositionValue())+ "; "+
				nf.format(getTransactionCost(investor.getTradingFee(), initialNumberOfShares))
				+"; "+nf.format(estimatedFinalPriceOfShare)+"; "+
				nf.format(getEstimatedFinalPositionValue());
			return str;
		}
		return null;
	}
	
	public String getCompanyTicker() {
		return getCompany().getID();
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Company getCompany() {
		return company;
	}

	public void setPriceOfShare(double priceOfShare) {
		this.priceOfShare = priceOfShare;
	}

	public double getPriceOfShare() {
		return priceOfShare;
	}

	public void setNumberOfShares(int quantity) {
		this.numberOfShares = quantity;
	}

	public int getNumberOfShares() {
		return numberOfShares;
	}

	public void setEstimatedFinalPriceOfShare(double estimatedFinalPriceOfShare) {
		if (estimatedFinalPriceOfShare<0) {
			estimatedFinalPriceOfShare=0;
		}
		this.estimatedFinalPriceOfShare = estimatedFinalPriceOfShare;
	}

	public double getEstimatedFinalPriceOfShare() {
		return estimatedFinalPriceOfShare;
	}

	public boolean isShort() {
		return (getNumberOfShares()<0);
	}
}