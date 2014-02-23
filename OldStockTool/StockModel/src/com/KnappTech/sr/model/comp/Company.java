package com.KnappTech.sr.model.comp;

import com.KnappTech.sr.model.Asset;
import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.beans.CompanyBean;
import com.KnappTech.sr.model.constants.CurrencyType;
//import com.KnappTech.sr.persistence.EconomicRecordManager;
//import com.KnappTech.sr.persistence.FinancialHistoryManager;
//import com.KnappTech.sr.persistence.IndustryManager;
//import com.KnappTech.sr.persistence.PriceHistoryManager;
//import com.KnappTech.sr.persistence.SectorManager;
//import com.KnappTech.sr.view.report.ReportManager;
//import com.KnappTech.util.Filter;
import com.KnappTech.util.Lock;
import com.KnappTech.util.SetOnceGuard;
//import com.KnappTech.view.report.Report;
//import com.KnappTech.view.report.ReportColumn;
//import com.KnappTech.view.report.ReportColumnFormat;
//import com.KnappTech.view.report.ReportRow;

public class Company implements Asset {
    public static final long serialVersionUID = 201001161702L;
    
    private final String id;
    private final short sector;
    private final short industry; 
    private final CurrencyType exchangeType;
	private final SetOnceGuard<PriceHistory> phg = new SetOnceGuard<PriceHistory>();
    private final SetOnceGuard<FinancialHistory> fhg = new SetOnceGuard<FinancialHistory>();
    private final SetOnceGuard<RegressionResults> rrg = new SetOnceGuard<RegressionResults>();
    private Currency lastKnownValue = null;
	private transient final Lock lock = new Lock();
    
	private Company(String tickerSymbol,short sector,short industry,CurrencyType exchangeType) {
		this.id = tickerSymbol;
		this.sector = sector;
		this.industry = industry;
		this.exchangeType = exchangeType;
	}
	
	private Company(String tickerSymbol,PriceHistory ph, FinancialHistory fh,
			short sector,short industry,CurrencyType exchangeType) {
		this.id = tickerSymbol;
		phg.setObject(ph);
		fhg.setObject(fh);
		this.sector = sector;
		this.industry = industry;
		this.exchangeType = exchangeType;
	}
	
	private Company(Company original) {
		this.id = original.id;
		this.sector = original.sector;
		this.industry = original.industry;
		PriceHistory pho = original.getPriceHistory();
		if (pho!=null) {
			PriceHistory ph = pho.clone();
			this.phg.setObject(ph);
			this.lock.addSubObject(ph);
		}
		FinancialHistory fho = original.getFinancialHistory();
		if (fho!=null){
			FinancialHistory fh = fho.clone();
			this.fhg.setObject(fh);
			this.lock.addSubObject(fh);
		}
		RegressionResults rso = original.getRegressionResultsSet();
		if (rso!=null) {
			RegressionResults rs = rso.clone();
			this.rrg.setObject(rs);
			this.lock.addSubObject(rs);
		}
		this.lastKnownValue = original.lastKnownValue;
		exchangeType = original.exchangeType;
		// leave it unlocked.
	}
	
	public static final Company create(String tickerSymbol,Sector sector,Industry industry) {
		if (tickerSymbol!=null && tickerSymbol.length()>0 && sector!=null && industry!=null) {
			return new Company(tickerSymbol,
					sector.getIDValue(),industry.getIDValue(),
					CurrencyType.USDOLLAR);
		}
		return null;
	}
	
	public static final Company create(String tickerSymbol,
			PriceHistory ph, FinancialHistory fh,
			Sector sector,Industry industry) {
		if (tickerSymbol!=null && tickerSymbol.length()>0 && sector!=null && industry!=null) {
			return new Company(tickerSymbol,ph,fh,sector.getIDValue(),
					industry.getIDValue(), CurrencyType.USDOLLAR);
		}
		return null;
	}
	
	public static final Company create(CompanyBean company) {
		Company c = new Company(company.getId(),company.getSector(),company.getIndustry(),CurrencyType.USDOLLAR);
		c.setRegressionResultsSet(RegressionResults.create(company.getRrg()));
		return c;
	}

	public static final Company createIndicator(String id) {
		if (id!=null && id.length()>0) {
			return new Company(id,(short)-1,(short)-1, CurrencyType.USDOLLAR);
		}
		return null;
	}
	
	public Company clone() {
		return new Company(this);
	}
	
	public void setPriceHistory(PriceHistory priceHistory) {
		if (lock.canEdit() && priceHistory!=null) {
			phg.setObject(priceHistory);
			if (!priceHistory.isEmpty()) {
				setLastKnownPrice(priceHistory.getLastValue());
			}
			lock.addSubObject(priceHistory);
		}
	}
	
	public PriceHistory getPriceHistory() {
		return phg.getObject();
	}
//	
//	public FinancialHistory passivelyUpdateFinancialHistory() {
//		if (lock.canEdit()) {
//			if (FinancialHistoryManager.acqIfLoaded(getID())!=null) {
//				fhg.setObject(FinancialHistoryManager.acqIfLoaded(getID()));
//			}
//		}
//		return getFinancialHistory();
//	}

	public void setFinancialHistory(FinancialHistory financialHistory) {
		if (lock.canEdit()) {
			fhg.setObject(financialHistory);
			lock.addSubObject(financialHistory);
		}
	}

	public FinancialHistory getFinancialHistory() {
		return fhg.getObject();
	}

	public void setRegressionResultsSet(RegressionResults regressionResultsSet) {
		if (canEdit()) {
			rrg.setObject(regressionResultsSet);
		}
	}
	
	public void addRegressionResults(RegressionResult results) {
		if (canEdit()) {
			if (rrg.isSet()) {
				RegressionResults rrs = rrg.getObject();
				rrs.add(results);
			} else {
				RegressionResults rs = new RegressionResults();
				rs.add(results);
				rrg.setObject(rs);
				lock.addSubObject(rs);
			}
		}
	}

	public RegressionResults getRegressionResultsSet() {
		return rrg.getObject();
	}
	
	@Override
	public int hashCode() {
		int i = getID().hashCode();
		if (i<Integer.MAX_VALUE) i++;
		else i=Integer.MIN_VALUE;
		return i;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Company) {
			Company company = (Company)obj;
			return (company.getID().equals(getID()));
		}
		return false;
	}

	public short getSector() {
		return sector;
	}

	public short getIndustry() {
		return industry;
	}

	@Override
	public String getID() {
		return id;
	}
	
//	public FinancialHistory loadFinancialHistory() {
//		if (getFinancialHistory()!=null)
//			return getFinancialHistory();
//		try {
//			setFinancialHistory(FinancialHistoryManager.getIfStored(getID()));
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		if (getFinancialHistory()==null) {
//			System.err.println("Warning: the financial history could not be loaded for: "+id);
//		}
//		return getFinancialHistory();
//	}
	
	@Override 
	public String toString() {
		return "ticker="+id;
	}
	
	@SuppressWarnings("unused")
	private void defaultExceptionHandler(Exception e) {
		System.err.format("An exception was thrown by thread %s, in class %s, method %s%n" +
				"   type: %s, message: %s%n" +
				"   ", Thread.currentThread().getName(),getClass(),
				Thread.currentThread().getStackTrace()[2].getMethodName(),
				e.getClass(),e.getMessage());
	}
	
	@Override
	public boolean isValid() { return validateCompany(); }
	
	private boolean validateCompany() {
		if (id==null || id.isEmpty()) {
			System.err.format("While trying to validate type: %s, the id was missing.",getClass().getName(),id);
			return false;
		} else {
			String problems = "";

			if (!problems.isEmpty()) {
				System.err.format("While validating the type: %s, with id: %s%n" +
						"   found the following problems: %n%s%n", getClass().getName(),id,problems);
				return false;
			}
		}
		return true;
	}

	public void setLastKnownValue(Currency lastKnownValue) {
		if (canEdit()) {
			this.lastKnownValue = lastKnownValue;
		}
	}

	public Currency getLastKnownValue() {
		return lastKnownValue;
	}

	public double getLastKnownPrice() {
		double lkp = -1;
		if (lastKnownValue!=null) {
			lkp = lastKnownValue.getQuantity();
		}
		if (lkp<=0) {
//			passivelyUpdatePriceHistory();
			if (getPriceHistory()!=null)
				lkp = getPriceHistory().getLastValue();
		}
		return lkp;
	}

	public void setLastKnownPrice(double lastKnownPrice) {
		if (canEdit()) {
			Currency currency = new Currency(exchangeType);
			currency.setQuantity(lastKnownPrice);
			this.lastKnownValue = currency;
		}
	}
	
	@Override
	public Currency getValueOfShare() {
		if (lastKnownValue!=null) {
			return lastKnownValue;
		} else if (getPriceHistory()!=null) {
			double value = getPriceHistory().getLastValue();
			Currency currency = new Currency(exchangeType);
			currency.setQuantity(value);
			return currency;
		}
		return null;
	}

	@Override
	public int compareTo(String o) {
		return getID().compareTo(o);
	}
	
	public String getIndustryName() {
		int ind = getIndustry();
		if (ind>=0) {
			return Industry.getName(ind);
		}
		return "";
	}
	
	public String getSectorName() {
		int ind = getSector();
		if (ind>=0) {
			return Sector.getName(ind);
		}
		return "";
	}
	
	public final synchronized boolean isLocked() {
		return lock.isLocked();
	}
	
	public final synchronized boolean canEdit() {
		return lock.canEdit();
	}
	
	public final synchronized void lock() {
		lock.lock();
	}
	
	public final synchronized void unlock() {
		lock.unlock();
	}
	
	public final synchronized void permanentlyLock() {
		lock.permanentlyLock();
	}
	
	public final boolean isPriceHistorySet() {
		return phg.isSet();
	}
	
	public final boolean isFinancialHistorySet() {
		return fhg.isSet();
	}
}