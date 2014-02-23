package com.KnappTech.sr.model.Financial;

import java.io.Serializable;

import com.KnappTech.model.KTObject;
import com.KnappTech.model.LiteDate;
//import com.KnappTech.sr.ctrl.parse.FinancialParser;
//import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.beans.FHBean;
//import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.constants.StatementType;
//import com.KnappTech.sr.persistence.FinancialEntryTypesManager;
//import com.KnappTech.sr.persistence.FinancialHistoryManager;
//import com.KnappTech.sr.persistence.PriceHistoryManager;
import com.KnappTech.util.CheckedVariable;
import com.KnappTech.util.DataPoints;
import com.KnappTech.util.Domain;
import com.KnappTech.util.DomainForInterval;
//import com.KnappTech.util.Filter;
import com.KnappTech.util.Lock;
//import com.KnappTech.view.report.ReportColumnFormat;
//import com.KnappTech.view.report.ReportRow;

public class FinancialHistory 
implements KTObject,Serializable {
	private static final long serialVersionUID = 201001161925L;
	public static final String NI = "Net Income";
	public static final String TL = "Total Liabilities";
	public static final String TE = "Total Shareholder Equity";
	public static final String TA = "Total Assets";
	public static final String CA = "Current Assets";
	public static final String DIV = "Cash Dividends Paid - Total";
	public static final String DIV2 = "Common Dividends";
	public static final String EARNINGS = "Net Revenues";
	public static final String SH = "Common Shares Outstanding";
	
	private final String id;
	private LiteDate lastUpdate = LiteDate.getOrCreate(2000,(byte)0,(byte)1);
	
	private final FinancialStatement balanceStatement;
	private final FinancialStatement incomeStatement;
	private final FinancialStatement cashStatement;
	
	private transient Lock lock = new Lock();
	
	private FinancialHistory(String id) {
		this.id = id;
		this.balanceStatement = new FinancialStatement(StatementType.BALANCE);
		this.incomeStatement = new FinancialStatement(StatementType.INCOME);
		this.cashStatement = new FinancialStatement(StatementType.CASH);
	}
	
	private FinancialHistory(FinancialHistory original) {
		this.id = original.id;
		this.balanceStatement = original.balanceStatement;
		this.incomeStatement = original.incomeStatement;
		this.cashStatement = original.cashStatement;
	}
	
	private FinancialHistory(FHBean fh) {
		this.id = fh.getId();
		this.lastUpdate = fh.getLastUpdate();
		this.balanceStatement = FinancialStatement.create(fh.getBalanceStatement());
		this.incomeStatement = FinancialStatement.create(fh.getIncomeStatement());
		this.cashStatement = FinancialStatement.create(fh.getCashStatement());
	}
	
	public static final FinancialHistory create(String id) {
		if (id!=null && id.length()>0) {
			return new FinancialHistory(id);
		}
		return null;
	}

	public static final FinancialHistory create(FHBean fh) {
		String id = fh.getId();
		if (id!=null && id.length()>0) {
//			FinancialEntryTypesManager.LoadIfNecessary();
			return new FinancialHistory(fh);
		}
		return null;
	}

	@Override
	public int compareTo(String o) {
		return id.compareTo(o);
	}

	@Override
	public String getID() {
		return id;
	}

	public CheckedVariable getTTMDividend() {
		Domain domain = Domain.negativeDomain();
		CheckedVariable div = getMostRecentTTMSum(StatementType.CASH, DIV, DIV2, null,domain);
		CheckedVariable positiveDiv = new CheckedVariable(Domain.positiveDomain());
		if (div.isValid())
			positiveDiv.setValue(-div.getValue());
		else 
			positiveDiv.setValue(0);
		return positiveDiv;
	}

	public CheckedVariable getTTMDividendPerShare() {
		CheckedVariable sh = getCommonShares();
		CheckedVariable div = getTTMDividend();
		Domain domain = Domain.positiveDomain();
		CheckedVariable cv = new CheckedVariable(domain);
		if (sh.isValid() && div.isValid()) {
			Double d = div.getValue()/sh.getValue();
			cv.setValue(d);
		}
		return cv;
	}
	
	public CheckedVariable getCommonShares() {
		Domain domain = Domain.greaterThanZeroDomain();
		CheckedVariable d = getMostRecentBalanceValue(SH, null, null,domain);
		if (!d.isValid()) {
			d = getMostRecentIncomeValue("Shares To Calculate EPS", 
					"Shares To Calculate EPS Diluted", null,domain);
		}
		return d;
	}

	public CheckedVariable getProfitMargin() {
		CheckedVariable ern = getTTMEarnings();
		CheckedVariable ni = getTTMNetIncome();
		Domain domain = Domain.realDomain();
		CheckedVariable cv = new CheckedVariable(domain);
		if (ern.isValid() && ern.getValue()>0 && ni.isValid()) {
			Double d = ni.getValue()/ern.getValue();
			cv.setValue(d);
		}
		return cv;
	}
	
	@SuppressWarnings({ "unused"})
	private void defaultExceptionHandler(Exception e) {
		System.err.format("An exception was thrown by thread %s, in class %s, method %s%n" +
				"   type: %s, message: %s%n" +
				"   ", Thread.currentThread().getName(),getClass(),
				Thread.currentThread().getStackTrace()[2].getMethodName(),
				e.getClass(),e.getMessage());
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof FinancialHistory) {
			FinancialHistory fh = (FinancialHistory)obj;
			return fh.getID().equals(getID());
		}
		return false;
	}
	
	public int hashCode() {
		return getID().hashCode()+1;
	}
	
	@Override
	public boolean isValid() { return true; }
	
	public CheckedVariable getMostRecentBalanceValue(String name, 
			String alt1, String alt2,Domain domain) {
		return getMostRecentValue(StatementType.BALANCE, name, alt1, alt2,domain);
	}
	
	public CheckedVariable getLastPeriodBalanceValue(String name, 
			String alt1, String alt2,Domain domain) {
		return getLastValueOnly(StatementType.BALANCE, name, alt1, alt2,domain);
	}
	
	public CheckedVariable getLastValueOnly(StatementType type, 
			String name, String alt1, String alt2,Domain domain) {
		FinancialStatement b = get(type);
		CheckedVariable v = b.getLastValueOnly(name,alt1,alt2,domain);
		return v;
	}
	
	public FinancialStatement get(StatementType type) {
		if (type.equals(StatementType.BALANCE)) {
			return getBalanceStatement();
		} else if (type.equals(StatementType.INCOME)) {
			return getIncomeStatement();
		} else if (type.equals(StatementType.CASH)) {
			return getCashStatement();
		} else {
			System.err.println("Requested an unsupported statement type.");
			return getBalanceStatement();
		}
	}

	public CheckedVariable getMostRecentTTMIncomeValue(String name, 
			String alt1, String alt2,Domain domain) {
		return getMostRecentTTMSum(StatementType.INCOME, name, alt1, alt2,domain);
	}
	
	public CheckedVariable getMostRecentIncomeValue(String name, 
			String alt1, String alt2,Domain domain) {
		return getMostRecentValue(StatementType.INCOME, name, alt1, alt2,domain);
	}
	
	public CheckedVariable getMostRecentValue(StatementType type,
			String name, String alt1, String alt2,Domain domain) {
		FinancialStatement b = get(type);
		CheckedVariable v = b.getMostRecentValue(name,alt1,alt2,domain);
		return v;
	}
	
	public CheckedVariable getMostRecentTTMSum(StatementType type,
			String name, String alt1, String alt2,Domain domain) {
		FinancialStatement b = get(type);
		CheckedVariable v = b.getMostRecentTTMSum(name,alt1,alt2,domain);
		return v;
	}
	
	public CheckedVariable getAnnualValue(StatementType type, String name, 
			Domain domain, int yearsPast) {
		FinancialStatement b = get(type);
		CheckedVariable v = b.getAnnualValue(name,domain,yearsPast);
		return v;
	}
	
	public CheckedVariable getPricePerEarnings(double lastKnownPrice) {
		CheckedVariable eps = getTTMEPS();
		Domain domain = Domain.greaterThanZeroDomain();
		CheckedVariable cv = new CheckedVariable(domain);
		if (eps.isValid() && eps.getValue()>0) {
			Double d = lastKnownPrice/eps.getValue();
			cv.setValue(d);
		}
		return cv;
	}
	
	public CheckedVariable getTTMEPS() {
		Domain domain = Domain.realDomain();
		CheckedVariable d = getMostRecentTTMIncomeValue("EPS", "EPS Diluted", null,domain);
		if (d.isValid())
			return d;
		CheckedVariable cv = new CheckedVariable(domain);
		d = getTTMEarnings();
		CheckedVariable sh = getCommonShares();
		if (d.isValid() && sh.isValid() && sh.getValue()>0) {
			Double db = d.getValue()/sh.getValue();
			cv.setValue(db);
		}
		return cv;
	}

	public CheckedVariable getReturnOnEquity() {
		CheckedVariable ni = getTTMNetIncome();
		CheckedVariable te = getTotalEquity();
		Domain domain = Domain.realDomain();
		CheckedVariable cv = new CheckedVariable(domain);
		if (ni.isValid() && te.isValid() && te.getValue()!=0) {
			Double d = ni.getValue()/te.getValue();
			cv.setValue(d);
		}
		return cv;
	}

	public CheckedVariable getReturnOnAssets() {
		CheckedVariable ni = getTTMNetIncome();
		CheckedVariable ta = getTotalAssets();
		Domain domain = Domain.realDomain();
		CheckedVariable cv = new CheckedVariable(domain);
		if (ni.isValid() && ta.isValid()) {
			Double d = ni.getValue()/ta.getValue();
			cv.setValue(d);
		}
		return cv;
	}
	
	public CheckedVariable getCurrentRatio() {
		CheckedVariable cl = getCurrentLiabilities();
		CheckedVariable ca = getCurrentAssets();
		Domain domain = Domain.positiveDomain();
		CheckedVariable cv = new CheckedVariable(domain);
		if (cl.isValid() && ca.isValid()) {
			Double d= ca.getValue()/cl.getValue();
			cv.setValue(d);
		}
		return cv;
	}
	
	public CheckedVariable getCurrentAssets()
	{
		Domain domain = Domain.positiveDomain();
		return getMostRecentBalanceValue(CA, null, null,domain);
	}
	
	public CheckedVariable getCurrentLiabilities() {
		Domain domain = Domain.positiveDomain();
		CheckedVariable ap = getLastPeriodBalanceValue("Accounts Payable", null, null,domain);
		CheckedVariable std = getLastPeriodBalanceValue("Short Term Debt", null, null,domain);
		CheckedVariable ae = getLastPeriodBalanceValue("Accrued Expenses", null, null,domain);
		CheckedVariable ocl = getLastPeriodBalanceValue("Other Current Liabilities", null, null,domain);
		Double d = new Double(0);
		if (ap.isValid()) {
			d+=ap.getValue();
		}
		if (std.isValid()) {
			d+=std.getValue();
		}
		if (ae.isValid()) {
			d+=ae.getValue();
		}
		if (ocl.isValid()) {
			d+=ocl.getValue();
		}
		CheckedVariable cv = new CheckedVariable(domain,d);
		return cv;
	}
	
	public CheckedVariable getTotalLiabilitiesPerTotalAssets() {
		CheckedVariable tl = getTotalLiabilities();
		CheckedVariable ta = getTotalAssets();
		Domain domain = new DomainForInterval(0,true,1000,true);
		CheckedVariable cv = new CheckedVariable(domain);
		if (ta.isValid() && tl.isValid()) {
			Double d = tl.getValue()/ta.getValue();
			cv.setValue(d);
		}
		return cv;
	}
	
	public CheckedVariable getTotalLiabilities() {
		Domain domain = Domain.greaterThanZeroDomain();
		CheckedVariable d = getMostRecentBalanceValue(TL, null, null,domain);
		if (!d.isValid()) {
			CheckedVariable ltd = getMostRecentBalanceValue(
					"Long Term Debt", null,null, domain);
			CheckedVariable dt = getMostRecentBalanceValue(
					"Deferred Taxes", null,null, domain);
			CheckedVariable cl = getCurrentLiabilities();
			Double db = new Double(0);
			if (ltd.isValid()) {db+=ltd.getValue();}
			if (dt.isValid()) {db+=dt.getValue();}
			if (cl.isValid()) {db+=cl.getValue();}
			d.setValue(db);
		}
		return d;
	}
	
	public CheckedVariable getTotalAssets() {
		Domain domain = Domain.positiveDomain();
		return getMostRecentBalanceValue(TA, null, null,domain);
	}
	
	public CheckedVariable getTotalEquity() {
		Domain domain = Domain.realDomain();
		return getLastPeriodBalanceValue(TE, "Common Equity", null,domain);
	}
	
	public CheckedVariable getTotalAssets(int yearsPast) {
		Domain domain = Domain.positiveDomain();
		return getAnnualValue(StatementType.BALANCE, TA, domain, yearsPast);
	}
	
	public CheckedVariable getTotalEquity(int yearsPast) {
		Domain domain = Domain.realDomain();
		CheckedVariable c = getAnnualValue(StatementType.BALANCE, TE, domain, yearsPast);
		if (c.isValid()) {
			c = getAnnualValue(StatementType.BALANCE, "Common Equity", domain, yearsPast);
		}
		return c;
	}
	
	public CheckedVariable getTTMNetIncome() {
		Domain domain = Domain.realDomain();
		return getMostRecentTTMIncomeValue(NI, null, null,domain);
	}
	
	public CheckedVariable getTTMNetIncome(int yearsPast) {
		Domain domain = Domain.realDomain();
		return getAnnualValue(StatementType.INCOME, NI, domain, yearsPast);
	}
	
	public CheckedVariable getTTMEarnings() {
		Domain domain = Domain.realDomain();
		return getMostRecentTTMIncomeValue(EARNINGS, null, null,domain);
	}
	
	public CheckedVariable getTTMEarnings(int yearsPast) {
		Domain domain = Domain.realDomain();
		return getAnnualValue(StatementType.INCOME, EARNINGS, domain, yearsPast);
	}

	public void setLastUpdate(LiteDate lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public LiteDate getLastUpdate() {
		return lastUpdate;
	}

	public FinancialStatement getBalanceStatement() {
		return balanceStatement;
	}

	public FinancialStatement getIncomeStatement() {
		return incomeStatement;
	}

	public FinancialStatement getCashStatement() {
		return cashStatement;
	}

	public boolean isEmpty() {
		return (incomeStatement.isEmpty() &&
				balanceStatement.isEmpty() &&
				cashStatement.isEmpty());
	}
	
	public FinancialStatementAtFrequency get(StatementType type,boolean quarterly) {
		return get(type).get(quarterly);
	}
	
	public String toString() {
		String str = getID()+" Financial History:\n";
		str+=incomeStatement.toString();
		str+=balanceStatement.toString();
		str+=cashStatement.toString();
		return str;
	}

	public CheckedVariable getMarketCapitalization(double lastKnownPrice) {
		CheckedVariable commonShares = getCommonShares();
		CheckedVariable mc = new CheckedVariable(Domain.positiveDomain());
		if (commonShares.isValid() && lastKnownPrice>0) {
			double mcv = lastKnownPrice*commonShares.getValue();
			mc.setValue(mcv);
		}
		if (!mc.isValid() || mc.getValue()<0) {
			mc.setValue(-1);
		}
		return mc;
	}
	
	public CheckedVariable getCashFlowFromOperations(int yearsPast) {
		Domain domain = Domain.realDomain();
		CheckedVariable cfo = new CheckedVariable(domain);
		CheckedVariable ni = getTTMNetIncome(yearsPast);
		CheckedVariable da = getDepreciationAndAmortization(yearsPast);
		CheckedVariable oncc = getOtherNonCashCharges(yearsPast);
		CheckedVariable inwc = getIncreaseInNetWorkingCapital(yearsPast);
		double sum = 0;
		if (ni.isValid()) { sum+=ni.getValue(); }
		if (da.isValid()) { sum+=da.getValue(); }
		if (oncc.isValid()) { sum+=oncc.getValue(); }
		if (inwc.isValid()) { sum-=inwc.getValue(); }
		cfo.setValue(sum);
		return cfo;
	}
	
	public CheckedVariable getCashFlowFromOperations() {
		Domain domain = Domain.realDomain();
		CheckedVariable cfo = new CheckedVariable(domain);
		CheckedVariable ni = getTTMNetIncome();
		CheckedVariable da = getDepreciationAndAmortization();
		CheckedVariable oncc = getOtherNonCashCharges();
		CheckedVariable inwc = getIncreaseInNetWorkingCapital();
		double sum = 0;
		if (ni.isValid()) { sum+=ni.getValue(); }
		if (da.isValid()) { sum+=da.getValue(); }
		if (oncc.isValid()) { sum+=oncc.getValue(); }
		if (inwc.isValid()) { sum-=inwc.getValue(); }
		cfo.setValue(sum);
		return cfo;
	}
	
	public CheckedVariable getTaxRate() {
		Domain domain = Domain.zeroToOneDomain();
		CheckedVariable a = getPreTaxIncome();
		CheckedVariable b = getTax();
		CheckedVariable tr = new CheckedVariable(domain);
		if (a.isValid() && b.isValid() && a.getValue()!=0) {
			tr.setValue(b.getValue()/a.getValue());
		} else {
			tr.setValue(0.5);
		}
		return tr;
	}
	
	public CheckedVariable getTaxRate(int yearsPast) {
		Domain domain = Domain.zeroToOneDomain();
		CheckedVariable a = getPreTaxIncome(yearsPast);
		CheckedVariable b = getTax(yearsPast);
		CheckedVariable tr = new CheckedVariable(domain);
		if (a.isValid() && b.isValid() && a.getValue()!=0) {
			tr.setValue(b.getValue()/a.getValue());
		} else {
			tr.setValue(0.5);
		}
		return tr;
	}
	
	public CheckedVariable getDepreciation() {
		Domain domain = Domain.positiveDomain();
		CheckedVariable a = new CheckedVariable(domain);
		a = getMostRecentTTMSum(StatementType.CASH, "Depreciation", null, null, domain);
		return a;
	}
	
	public CheckedVariable getAmortization() {
		Domain domain = Domain.positiveDomain();
		CheckedVariable a = new CheckedVariable(domain);
		return a;
	}
	
	public CheckedVariable getDepreciation(int yearsPast) {
		Domain domain = Domain.positiveDomain();
		CheckedVariable a = new CheckedVariable(domain);
		a = getAnnualValue(StatementType.CASH, "Depreciation", domain, yearsPast);
		return a;
	}
	
	public CheckedVariable getAmortization(int yearsPast) {
		Domain domain = Domain.positiveDomain();
		CheckedVariable a = new CheckedVariable(domain);
		return a;
	}
	
	public CheckedVariable getDepreciationAndAmortization() {
		Domain domain = Domain.positiveDomain();
		CheckedVariable a = new CheckedVariable(domain);
		a = getMostRecentTTMIncomeValue("Depreciation and Amortization", null, null, domain);
		if (!a.isValid()) {
			CheckedVariable b = getDepreciation();
			CheckedVariable c = getAmortization();
			int sum = 0;
			if (b.isValid()) { sum+=b.getValue(); }
			if (c.isValid()) { sum+=c.getValue(); }
			a.setValue(sum);
		}
		return a;
	}
	
	public CheckedVariable getDepreciationAndAmortization(int yearsPast) {
		Domain domain = Domain.positiveDomain();
		CheckedVariable a = new CheckedVariable(domain);
		a = getAnnualValue(StatementType.INCOME, 
				"Depreciation and Amortization", domain, yearsPast);
		if (!a.isValid()) {
			CheckedVariable b = getDepreciation(yearsPast);
			CheckedVariable c = getAmortization(yearsPast);
			int sum = 0;
			if (b.isValid()) { sum+=b.getValue(); }
			if (c.isValid()) { sum+=c.getValue(); }
			a.setValue(sum);
		}
		return a;
	}
	
	public CheckedVariable getInterest() {
		Domain domain = Domain.positiveDomain();
		CheckedVariable a = getMostRecentTTMIncomeValue(
				"Interest Expense On Debt", null, null, domain);
		if (!a.isValid()) {
			a.setValue(0);
		}
		return a;
	}
	
	public CheckedVariable getInterest(int yearsPast) {
		Domain domain = Domain.positiveDomain();
		CheckedVariable a = getAnnualValue(StatementType.INCOME, 
				"Interest Expense On Debt", domain, yearsPast); 
		if (!a.isValid()) {
			a.setValue(0);
		}
		return a;
	}
	
	public CheckedVariable getEBIDA() {
		CheckedVariable ni = getTTMNetIncome();
		CheckedVariable in = getInterest();
		CheckedVariable da = getDepreciationAndAmortization();
		double sum = 0;
		if (ni.isValid()) { sum+=ni.getValue(); }
		if (in.isValid()) { sum+=in.getValue(); }
		if (da.isValid()) { sum+=da.getValue(); }
		CheckedVariable a = new CheckedVariable(Domain.positiveDomain());
		a.setValue(sum);
		return a;
	}
	
	public CheckedVariable getEBDA() {
		CheckedVariable ni = getTTMNetIncome();
		CheckedVariable da = getDepreciationAndAmortization();
		double sum = 0;
		if (ni.isValid()) { sum+=ni.getValue(); }
		if (da.isValid()) { sum+=da.getValue(); }
		CheckedVariable a = new CheckedVariable(Domain.positiveDomain());
		a.setValue(sum);
		return a;
	}
	
	public CheckedVariable getEBIT() {
		return new CheckedVariable(Domain.realDomain());
	}
	
	public CheckedVariable getIncreaseInNetWorkingCapital(int yearsPast) {
		CheckedVariable a = getNetWorkingCapital(0+yearsPast);
		CheckedVariable b = getNetWorkingCapital(1+yearsPast);
		CheckedVariable c = new CheckedVariable(Domain.realDomain());
		if (a.isValid() && b.isValid()) {
			c.setValue(a.getValue()-b.getValue());
		}
		return c;
	}
	
	public CheckedVariable getIncreaseInNetWorkingCapital() {
		CheckedVariable a = getNetWorkingCapital(0);
		CheckedVariable b = getNetWorkingCapital(1);
		CheckedVariable c = new CheckedVariable(Domain.realDomain());
		if (a.isValid() && b.isValid()) {
			c.setValue(a.getValue()-b.getValue());
		}
		return c;
	}
	
	public CheckedVariable getTax() {
		Domain domain = Domain.realDomain();
		CheckedVariable t = getMostRecentTTMIncomeValue("Income Taxes", null, null, domain);
		return t;
	}
	
	public CheckedVariable getPreTaxIncome() {
		Domain domain = Domain.realDomain();
		CheckedVariable t = getMostRecentTTMIncomeValue("Pre-Tax Income", null, null, domain);
		return t;
	}
	
	public CheckedVariable getTax(int yearsPast) {
		Domain domain = Domain.realDomain();
		CheckedVariable t = getAnnualValue(StatementType.INCOME, 
				"Income Taxes", domain, yearsPast);
		return t;
	}
	
	public CheckedVariable getPreTaxIncome(int yearsPast) {
		Domain domain = Domain.realDomain();
		CheckedVariable t = getAnnualValue(StatementType.INCOME, 
				"Pre-Tax Income", domain, yearsPast);
		return t;
	}
	
	public CheckedVariable getCapitalExpenditures() {
		Domain domain = Domain.realDomain();
		CheckedVariable a = new CheckedVariable(domain);
		a = getMostRecentTTMSum(StatementType.CASH, "Capital Expenditures", null, null, domain);
		return a;
	}
	
	public CheckedVariable getCapitalExpenditures(int yearsPast) {
		Domain domain = Domain.realDomain();
		CheckedVariable a = new CheckedVariable(domain);
		a = getAnnualValue(StatementType.CASH, "Capital Expenditures", domain, yearsPast);
		return a;
	}
	
	public CheckedVariable getOtherNonCashCharges() {
		CheckedVariable a = new CheckedVariable(Domain.realDomain());
		return a;
	}
	
	public CheckedVariable getOtherNonCashCharges(int yearsPast) {
		CheckedVariable a = new CheckedVariable(Domain.realDomain());
		return a;
	}
	
	public CheckedVariable getNetBorrowing() {
		Domain domain = Domain.realDomain();
		CheckedVariable a = new CheckedVariable(domain);
		CheckedVariable ndf = getNewDebtFinancing();
		CheckedVariable dr = getDebtRepayment();
		double sum = 0;
		if (ndf.isValid()) { sum+=ndf.getValue(); }
		if (dr.isValid()) { sum+=dr.getValue(); }
		a.setValue(sum);
		return a;
	}
	
	public CheckedVariable getNetBorrowing(int yearsPast) {
		Domain domain = Domain.realDomain();
		CheckedVariable a = new CheckedVariable(domain);
		CheckedVariable ndf = getNewDebtFinancing(yearsPast);
		CheckedVariable dr = getDebtRepayment(yearsPast);
		double sum = 0;
		if (ndf.isValid()) { sum+=ndf.getValue(); }
		if (dr.isValid()) { sum+=dr.getValue(); }
		a.setValue(sum);
		return a;
	}
	
	public CheckedVariable getFreeCashFlowToEquity() {
		Domain domain = Domain.realDomain();
		CheckedVariable cfo = getCashFlowFromOperations();
		// the capitalExpenditures is naturally negative.
		CheckedVariable capitalExpenditures = getCapitalExpenditures();
		CheckedVariable netBorrowing = getNetBorrowing();
		double sum = 0;
		if (cfo.isValid()) { sum+=cfo.getValue(); }
		// since the capitalExpenditures is negative, we add instead of subtract.
		if (capitalExpenditures.isValid()) { sum+=capitalExpenditures.getValue(); } 
		if (netBorrowing.isValid()) { sum+=netBorrowing.getValue(); }
		CheckedVariable fcfe = new CheckedVariable(domain);
		fcfe.setValue(sum);
		return fcfe;
	}
	
	public CheckedVariable getFreeCashFlowToEquity(int yearsPast) {
		Domain domain = Domain.realDomain();
		CheckedVariable cfo = getCashFlowFromOperations(yearsPast);
		// the capitalExpenditures is naturally negative.
		CheckedVariable capitalExpenditures = getCapitalExpenditures(yearsPast);
		CheckedVariable netBorrowing = getNetBorrowing(yearsPast);
		double sum = 0;
		if (cfo.isValid()) { sum+=cfo.getValue(); }
		// since the capitalExpenditures is negative, we add instead of subtract.
		if (capitalExpenditures.isValid()) { sum+=capitalExpenditures.getValue(); } 
		if (netBorrowing.isValid()) { sum+=netBorrowing.getValue(); }
		CheckedVariable fcfe = new CheckedVariable(domain);
		fcfe.setValue(sum);
		return fcfe;
	}
	
	public CheckedVariable getFreeCashFlowToFirm(int yearsPast) {
		Domain domain = Domain.realDomain();
		CheckedVariable cfo = getCashFlowFromOperations(yearsPast);
		CheckedVariable capitalExpenditures = getCapitalExpenditures(yearsPast);
		CheckedVariable interest = getInterest(yearsPast);
		CheckedVariable taxRate = getTaxRate(yearsPast);
		double sum = 0;
		if (cfo.isValid()) { sum+=cfo.getValue(); }
		if (interest.isValid() && taxRate.isValid()) { 
			double i = interest.getValue();
			double tr = taxRate.getValue();
			double v = i*(1-tr);
			sum+=v;
		}
		if (capitalExpenditures.isValid()) { sum-=capitalExpenditures.getValue(); }
		CheckedVariable fcff = new CheckedVariable(domain);
		fcff.setValue(sum);
		return fcff;
	}
	
	public CheckedVariable getFreeCashFlowToFirm() {
		Domain domain = Domain.realDomain();
		CheckedVariable cfo = getCashFlowFromOperations();
		CheckedVariable capitalExpenditures = getCapitalExpenditures();
		CheckedVariable interest = getInterest();
		CheckedVariable taxRate = getTaxRate();
		double sum = 0;
		if (cfo.isValid()) { sum+=cfo.getValue(); }
		if (interest.isValid() && taxRate.isValid()) { 
			double i = interest.getValue();
			double tr = taxRate.getValue();
			double v = i*(1-tr);
			sum+=v;
		}
		if (capitalExpenditures.isValid()) { sum-=capitalExpenditures.getValue(); }
		CheckedVariable fcff = new CheckedVariable(domain);
		fcff.setValue(sum);
		return fcff;
	}
	
	public CheckedVariable getCurrentAssets(int yearsPast) {
		Domain domain = Domain.positiveDomain();
		CheckedVariable a = getAnnualValue(StatementType.BALANCE, 
				"Current Assets", domain, yearsPast);
		return a;
	}
	
	public CheckedVariable getCurrentLiabilities(int yearsPast) {
		Domain domain = Domain.positiveDomain();
		CheckedVariable c = getAnnualValue(StatementType.BALANCE, 
				"Other Liabilities", domain, yearsPast);
		if (c.isValid()) {
			return c;
		} else {
			CheckedVariable v = new CheckedVariable(domain);
			CheckedVariable a = getAnnualValue(StatementType.BALANCE, 
					"Accounts Payable", domain, yearsPast);
			CheckedVariable b = getAnnualValue(StatementType.BALANCE, 
					"Short Term Debt", domain, yearsPast);
			CheckedVariable d = getAnnualValue(StatementType.BALANCE, 
					"Other Current Liabilities", domain, yearsPast);
			double sum = 0;
			if (a.isValid()) {sum+=a.getValue(); }
			if (b.isValid()) {sum+=b.getValue(); }
			if (d.isValid()) {sum+=d.getValue(); }
			v.setValue(sum);
			return v;
		}
	}
	
	public CheckedVariable getTotalLiabilities(int yearsPast) {
		Domain domain = Domain.positiveDomain();
		CheckedVariable a = getAnnualValue(StatementType.BALANCE, 
				"Total Liabilities", domain, yearsPast);
		return a;
	}
	
	public CheckedVariable getNetWorkingCapital(int yearsPast) {
		Domain domain = Domain.realDomain();
		CheckedVariable a = getCurrentAssets(yearsPast);
		CheckedVariable b = getCurrentLiabilities(yearsPast);
		double sum = 0;
		if (a.isValid()) { sum+=a.getValue(); }
		if (b.isValid()) { sum-=b.getValue(); }
		CheckedVariable v = new CheckedVariable(domain);
		v.setValue(sum);
		return v;
	}
	
	public CheckedVariable getNewDebtFinancing() {
		Domain domain = Domain.realDomain();
		CheckedVariable a = getTotalLiabilities(0);
		CheckedVariable b = getTotalLiabilities(1);
		CheckedVariable c = new CheckedVariable(domain);
		if (a.isValid() && b.isValid()) {
			double d = a.getValue()-b.getValue();
			if (d<0) d=0;
			c.setValue(d);
		}
		return c;
	}
	
	public CheckedVariable getNewDebtFinancing(int yearsPast) {
		Domain domain = Domain.realDomain();
		CheckedVariable a = getTotalLiabilities(0+yearsPast);
		CheckedVariable b = getTotalLiabilities(1+yearsPast);
		CheckedVariable c = new CheckedVariable(domain);
		if (a.isValid() && b.isValid()) {
			double d = a.getValue()-b.getValue();
			if (d<0) d=0;
			c.setValue(d);
		}
		return c;
	}
	
	public CheckedVariable getDebtRepayment() {
		Domain domain = Domain.realDomain();
		CheckedVariable a = getTotalLiabilities(0);
		CheckedVariable b = getTotalLiabilities(1);
		CheckedVariable c = new CheckedVariable(domain);
		if (a.isValid() && b.isValid()) {
			double d = b.getValue()-a.getValue();
			if (d<0) d=0;
			c.setValue(d);
		}
		return c;
	}
	
	public CheckedVariable getDebtRepayment(int yearsPast) {
		Domain domain = Domain.realDomain();
		CheckedVariable a = getTotalLiabilities(0+yearsPast);
		CheckedVariable b = getTotalLiabilities(1+yearsPast);
		CheckedVariable c = new CheckedVariable(domain);
		if (a.isValid() && b.isValid()) {
			double d = b.getValue()-a.getValue();
			if (d<0) d=0;
			c.setValue(d);
		}
		return c;
	}
	
	public CheckedVariable getMarketCapPerFCFE(double lastKnownPrice) {
		Domain domain = Domain.positiveDomain();
		CheckedVariable a = new CheckedVariable(domain);
		CheckedVariable b = getMarketCapitalization(lastKnownPrice);
		double c = getWeightedFCFE();
		if (b.isValid()) {
			double v = b.getValue()/c;
			a.setValue(v);
		} else {
			a.setValue(-999);
		}
		return a;
	}
	
	public CheckedVariable getMarketCapPerTotalEquity(double lastKnownPrice) {
		Domain domain = Domain.positiveDomain();
		CheckedVariable a = new CheckedVariable(domain);
		CheckedVariable b = getMarketCapitalization(lastKnownPrice);
		CheckedVariable c = getTotalEquity();
		if (b.isValid() && c.isValid() && c.getValue()>0) {
			double v = b.getValue()/c.getValue();
			a.setValue(v);
		} else {
			a.setValue(-999);
		}
		return a;
	}
	
	public double getInvestorsExpectedReturnRate(double beta) {
		double b = beta;
		if (beta>4) b = 3;
		return 0.035+(0.12-0.035)*b;
	}
	
	public double getWeightedFCFE() {
		double fcfe1 = getFreeCashFlowToEquity(2).getValue();
		double fcfe2 = getFreeCashFlowToEquity(1).getValue();
		double fcfe3 = getFreeCashFlowToEquity().getValue();
		int size = 0;
		if (isValidFCFE(fcfe1)) {
			size++;
		}
		if (isValidFCFE(fcfe2)) {
			size++;
		}
		if (isValidFCFE(fcfe3)) {
			size++;
		}
		double wfcfe = 0;
		if (size==3) { // all three are valid points.
			wfcfe = (5*fcfe3+4*fcfe2+3*fcfe1)/(12);
		} else if (size==2) {
			if (isValidFCFE(fcfe3) && isValidFCFE(fcfe2)) {
				wfcfe = (5*fcfe3+4*fcfe2)/(9);
			}
			if (isValidFCFE(fcfe2) && isValidFCFE(fcfe1)) {
				// assume last year was zero.
				wfcfe = (4*fcfe2+3*fcfe1)/(12);
			}
			if (isValidFCFE(fcfe3) && isValidFCFE(fcfe1)) {
				// assume 2 years ago was zero.
				wfcfe = (5*fcfe3+3*fcfe1)/(12);
			}
		} else if (size==1) {
			if (isValidFCFE(fcfe3)) {
				wfcfe=fcfe3;
			} else if (isValidFCFE(fcfe2)) {
				// assume last year was zero
				wfcfe = (fcfe2*4)/(9);
			} else if (isValidFCFE(fcfe1)) {
				// assume last two years were zero
				wfcfe = (fcfe1*3)/(12);
			}
		} // if size==0, return 0.
		return wfcfe;
	}
	
	public double getWeightedInitialGrowth() {
		double initialGrowth = 0.05;
		double fcfe1 = getFreeCashFlowToEquity(2).getValue();
		double fcfe2 = getFreeCashFlowToEquity(1).getValue();
		double fcfe3 = getFreeCashFlowToEquity().getValue();
		int size = 0;
		if (isValidFCFE(fcfe1)) {
			size++;
		}
		if (isValidFCFE(fcfe2)) {
			size++;
		}
		if (isValidFCFE(fcfe3)) {
			size++;
		}
		double wfcfe = getWeightedFCFE();
		double slope = 0;
		if (size==3) { // all three are valid points.
			double[] x = {1,2,3};
			double[] y = new double[3];
			y[0]=fcfe1;
			y[1]=fcfe2;
			y[2]=fcfe3;
			DataPoints dp = new DataPoints(x,y);
			slope = dp.getSlope();
			initialGrowth = slope/wfcfe;
		} else if (size==2) {
			if (isValidFCFE(fcfe1) && isValidFCFE(fcfe2)) {
				slope = (fcfe2-fcfe1);
			}
			if (isValidFCFE(fcfe2) && isValidFCFE(fcfe3)) {
				slope = (fcfe3-fcfe2);
			}
			if (isValidFCFE(fcfe1) && isValidFCFE(fcfe3)) {
				slope = (fcfe3-fcfe1)/2;
			}
			initialGrowth = slope/wfcfe;
		}
		return initialGrowth;
	}
	
	private boolean isValidFCFE(double fcfe) {
		return (fcfe!=Double.MAX_VALUE && fcfe!=Double.MIN_VALUE &&
				fcfe!=-Double.MAX_VALUE && fcfe!=-Double.MIN_VALUE &&
				fcfe!=0);
	}
	
	public double getConservativeInitialGrowth() {
		double adjustedInitialGrowth = getWeightedInitialGrowth();
		if (adjustedInitialGrowth>0.14) {
			adjustedInitialGrowth = 0.14;
		} else if (adjustedInitialGrowth<-0.14) {
			adjustedInitialGrowth = -0.14;
		}
		return adjustedInitialGrowth;
	}
	
	public double getPresentValue(double beta, double lastKnownPrice,int years) {
		double ror = getInvestorsExpectedReturnRate(beta);
		double wfcfe = getWeightedFCFE();
		double initialGrowth = getConservativeInitialGrowth();
		double pv = 0;
		if (wfcfe>0)
		{	// assume growth
			double cumulativeGrowth = 1;
			for (int i = 0;i<years;i++) {
				double currentGrowth = 0.032+(initialGrowth-0.032)*Math.pow(Math.E, -i/3);
				cumulativeGrowth = cumulativeGrowth*(1+currentGrowth);
				double den = Math.pow(1+ror,i);
				double v = cumulativeGrowth*wfcfe/den;
				pv+=v;
			}
			return pv;
		} else 
		{	// seems like this company is failing to make any money.
			return -1;
		}
	}
	
	public int getImpliedYearsRemaining(double beta, double lastKnownPrice) {
		double ror = getInvestorsExpectedReturnRate(beta);
		double wfcfe = getWeightedFCFE();
		double initialGrowth = getConservativeInitialGrowth();
		CheckedVariable mccv = getMarketCapitalization(lastKnownPrice);
//		if (!mccv.isValid()) {
//			mccv = getMarketCapitalization(lastKnownPrice);
//		}
		double mc = 0;
		if (mccv.isValid()) {
			mc = mccv.getValue();
		}
		double pv = 0;
		if (wfcfe>0)
		{	// assume growth
			double cumulativeGrowth = 1;
			for (int i = 0;i<100;i++) {
				double currentGrowth = 0.032+(initialGrowth-0.032)*Math.pow(Math.E, -(double)i/3);
				cumulativeGrowth = cumulativeGrowth*(1+currentGrowth);
				double den = Math.pow(1+ror,i);
				double v = cumulativeGrowth*wfcfe/den;
				pv+=v;
				if (pv>mc) {
					return i;
				}
			}
			return 100;
		} else 
		{	// seems like this company is failing to make any money.
			return 110;
		}
	}
	
//	public void update() throws InterruptedException {
//		FinancialParser fp = new FinancialParser();
//		fp.update(this);
//		FinancialHistoryManager.save(this, true);
//	}
	
	public final synchronized boolean isLocked() {
		return lock.isLocked();
	}
	
	public final synchronized boolean canEdit() {
		return lock.canEdit();
	}
	
	public final synchronized void lock() {
		lock.lock();
		this.balanceStatement.permanentlyLock();
		this.incomeStatement.permanentlyLock();
		this.cashStatement.permanentlyLock();
	}
	
	public final synchronized void unlock() {
		lock.unlock();
	}
	
	public final synchronized void permanentlyLock() {
		lock.permanentlyLock();
	}
	
	public final synchronized FinancialHistory clone() {
		return new FinancialHistory(this);
	}

	public boolean hasEntries() {
		return balanceStatement.hasEntries() || incomeStatement.hasEntries() || cashStatement.hasEntries();
	}
	
	public boolean hasAllStatements() {
		return balanceStatement.hasAllFrequencies() && 
		incomeStatement.hasAllFrequencies() && 
		cashStatement.hasAllFrequencies();
	}

	public CheckedVariable getMarketCapitalizationPerTotalAssets(
			double lastValue) {
		CheckedVariable mc = getMarketCapitalization(lastValue);
		CheckedVariable ta = getTotalAssets();
		CheckedVariable mcta = new CheckedVariable(Domain.greaterThanZeroDomain());
		if (mc.isValid() && ta.isValid()) {
			mcta.setValue(mc.getValue()/ta.getValue());
		} else {
			mcta.setValue(99999);
		}
		return mcta;
	}
}