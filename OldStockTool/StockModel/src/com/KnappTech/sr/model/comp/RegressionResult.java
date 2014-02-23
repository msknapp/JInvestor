package com.KnappTech.sr.model.comp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.model.KTObject;
import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.AbstractSKTSet;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.RecordList;
import com.KnappTech.sr.model.Regressable.RecordSet;
import com.KnappTech.sr.model.beans.ERStatusBean;
import com.KnappTech.sr.model.beans.IndicatorBean;
import com.KnappTech.sr.model.beans.RegressionResultBean;
import com.KnappTech.sr.model.constants.RegressionMethod;

public class RegressionResult extends AbstractSKTSet<Indicator> implements
		KTObject
{
	private static final long serialVersionUID = 201001162015L;
	private final LiteDate creationDate;
    public final double regressandVariance;
	private final double coefficientOfDetermination;
    private final RegressionMethod regressionMethod;
	private double estimateValue = -1;
	private LiteDate estimateDate = LiteDate.EPOCH;
	
	private RegressionResult(double var, double cod,String id) {
		super(id);
		creationDate = LiteDate.getOrCreate();
		regressandVariance = var;
		coefficientOfDetermination = cod;
		regressionMethod = RegressionMethod.COEFFOFDET;
	}
	
	private RegressionResult(double var, double cod,RegressionMethod rm,String id) {
		super(id);
		creationDate = LiteDate.getOrCreate();
		regressandVariance = var;
		coefficientOfDetermination = cod;
		if (rm==null) 
			regressionMethod=RegressionMethod.COEFFOFDET;
		else 
			regressionMethod = rm;
	}
	
	private RegressionResult(RegressionResultBean rb) {
		super(rb.getID());
		creationDate = rb.getCreationDate();
		regressandVariance = rb.getRegressandVariance();
		coefficientOfDetermination = rb.getCoefficientOfDetermination();
		RegressionMethod rm = RegressionMethod.getFromIndex(rb.getRegressionMethod());
		if (rm==null) 
			regressionMethod = RegressionMethod.COEFFOFDET;
		else 
			regressionMethod = rm;
		estimateValue = rb.getEstimateValue();
		estimateDate = rb.getEstimateDate();
		if (creationDate==null)
			throw new NullPointerException("Given a null creation date in bean.");
		List<IndicatorBean> ls = rb.getIndicators();
		List<Indicator> tempList = new ArrayList<Indicator>(ls.size());
		for (IndicatorBean ib : ls) {
			Indicator i = Indicator.create(ib);
			if (i!=null) 
				tempList.add(i);
		}
		addAll(tempList);
	}
	
	public static final RegressionResult create(double var, double cod, RegressionMethod rm,String id) {
		if (var>0 && cod>0 && cod<1 && rm!=null) {
			return new RegressionResult(var,cod,rm,id);
		}
		throw new IllegalArgumentException("variance must be positive, and the "+
				"coefficient of determination must be between 0 and 1");
	}
	
	public static final RegressionResult create(double var, double cod,String id) {
		return create(var,cod,RegressionMethod.COEFFOFDET,id);
	}
	
	public static final RegressionResult create(RegressionResultBean rb) {
		if (rb==null)
			return null;
		if (rb.getCreationDate()==null)
			return null;
		if (rb.getCreationDate().before(LiteDate.getOrCreate(2010, (byte)Calendar.JANUARY,(byte)1)))
			return null;
		if (rb.getRegressandVariance()<=0 || rb.getCoefficientOfDetermination()<=0 || 
				rb.getCoefficientOfDetermination()>=1)
			throw new IllegalArgumentException("variance must be positive, and the "+
			"coefficient of determination must be between 0 and 1");
		if (rb.getRegressionMethod()<0)
			System.err.println("Have regression result bean with unknown index.");
		RegressionResult rrr = new RegressionResult(rb);
		if (rrr.isValid())
			return rrr;
		return null;
	}
	
	@Override
	public String toString() {
		String str = "";
		double sum = 0;
		for (Indicator indicator : items) {
			str += indicator.toString() + "\n";
			sum+=indicator.getProduct();
		}
		if (str.isEmpty())
			str = "I am an empty regression.";
		else
			str+="Estimate: "+sum;
		return str;
	}
	
    public double getRegressandVariance() {
		return regressandVariance;
	}

    public LinkedList<Double> getRegressionParameters() {
		LinkedList<Double> factors = new LinkedList<Double>();
		Iterator<Indicator> iter = items.iterator();
		while (iter.hasNext())
			factors.add(iter.next().getFactor());
		return factors;
	}

	public LinkedList<Double> getRegressionParameterStandardErrors() {
		LinkedList<Double> errors = new LinkedList<Double>();
		Iterator<Indicator> iter = items.iterator();
		while (iter.hasNext())
			errors.add(iter.next().getError());
		return errors;
	}

	public RegressionMethod getRegressionMethod() {
		return regressionMethod;
	}

	public LiteDate getCreationDate() {
		return creationDate;
	}

	@Override
	public String getID() {
		return super.getID();
	}
	
	/**
	 * Has the estimate calculated and then stored locally so it can
	 * be used later if necessary.  Also updates the estimate date.
	 */
	public void updateEstimate() {
		estimateValue = calculateEstimate();
		estimateDate = LiteDate.getOrCreate();
	}
	
	/**
	 * Performs calculation to determine the current estimate.  Does 
	 * not store the result anywhere, for that use updateEstimate 
	 * instead.
	 * @return
	 */
	public double calculateEstimate() {
//		if (!updateLastValues())
//			System.err.println("Warning: insufficient data to produce an estimate.");
		double sum = 0;
		if (!lastValuesSet())
			throw new IllegalStateException("You must set the last values of " +
					"indicators before calculating an estimate.");
		for (Indicator indicator : items)
			sum+=indicator.getProduct();
		return sum;
	}
	
	private boolean lastValuesSet() {
		for (Indicator ind : this.items) 
			if (!ind.isLastValueSet())
				return false;
		return true;
	}
	
	public double getZ(double x, double average) {
		return getZ(x,average,getStandardDeviation());
	}
	
	public double getStandardDeviation() {
		return Math.sqrt(regressandVariance);
	}

	public static double getZ(double x, double average, double standardDeviation) {
		if (standardDeviation<=0)
			throw new IllegalArgumentException("tried dividing by zero to determine Z.");
		double z = (x-average)/standardDeviation;
		return z;
	}
	
	public static double getProbabilityIncrease(double z) {
		NormalDistributionImpl n = new NormalDistributionImpl(0,1);
		try {
			return 1-n.cumulativeProbability(z);
		} catch (MathException e) {
			defaultStaticExceptionHandler(e);
		}
		return 0;
	}

	public static double getROI(double lv, double estimateValue) {
		return 100*((estimateValue/lv)-1);
	}

	public void setIndicators(Collection<Indicator> indicators) {
		items.clear();
		addAll(new LinkedHashSet<Indicator>(indicators));
	}
	
	public LinkedHashSet<String> getERIndicatorIDs() {
		LinkedHashSet<Indicator> indicators = getERIndicators();
		Iterator<Indicator> iter = indicators.iterator();
		LinkedHashSet<String> ids = new LinkedHashSet<String>();
		while (iter.hasNext())
			ids.add(iter.next().getID());
		return ids;
	}

	public LinkedHashSet<Indicator> getERIndicators() {
		LinkedHashSet<Indicator> erIndicators = new LinkedHashSet<Indicator>();
		for (Indicator erInd : items)
			if (erInd.getTypeClass().equals(EconomicRecord.class))
				erIndicators.add(erInd);
		return erIndicators;
	}
	
	public LinkedHashSet<Indicator> getIndicators() {
		return new LinkedHashSet<Indicator>(items);
	}
	
	public boolean updateIndicatorsValue(RecordSet<?> records) {
		boolean allWorked= true;
		for (Indicator indicator : items) {
			try {
				RecordList<?> r = records.get(indicator.getID());
				if (r==null)
					r=records.getUnordered(indicator.getID());
				if (r==null) {
					allWorked = false;
					continue;
				}
				if (indicator.isDerivative()) {
					// the prediction is calling for the derivative value.
					if (!r.isDerivative())
						r=r.getDerivative();
				} else if (r.isDerivative()) {
					// here the record is a derivative but indicator is calling for 
					// a non-derivative.
					throw new IllegalArgumentException(
							"Given a record that is a derivative to update " +
							"an indicators value.");
				}
				indicator.setLastValue(r.getLastValue());
			} catch (Exception e) {
				e.printStackTrace();
				allWorked = false;
			}
		}
		return allWorked;
	}

	public boolean updateIndicatorsValue(IdentifiableList<ERStatusBean,String> records) {
		boolean allWorked= true;
		for (Indicator indicator : this.items) {
			try {
				ERStatusBean r = records.get(indicator.getID());
				if (r==null)
					r=records.getUnordered(indicator.getID());
				if (r==null) {
					allWorked = false;
					continue;
				}
				double lv = (indicator.isDerivative() ? r.getLastDerivativeValue() : r.getLastValue());
				indicator.setLastValue(lv);
			} catch (Exception e) {
				e.printStackTrace();
				allWorked = false;
			}
		}
		return allWorked;
	}
	
	public LinkedHashSet<String> getIndicatorIDs() {
		LinkedHashSet<String> indicators = new LinkedHashSet<String>();
		Iterator<Indicator> iter = items.iterator();
		while (iter.hasNext())
			indicators.add(iter.next().getID());
		return indicators;
	}

	public void setEstimateValue(double estimateValue) {
		this.estimateValue = estimateValue;
	}
	
	public double getUpToDateEstimate() {
		LiteDate present = LiteDate.getOrCreate();
		if (estimateValue<0 || estimateDate==null || estimateDate.before(present))
			updateEstimate();
		return estimateValue;
	}

	public double getEstimateValue() {
		return estimateValue;
	}

	public void setEstimateDate(LiteDate estimateDate) {
		this.estimateDate = estimateDate;
	}

	public LiteDate getEstimateDate() {
		return estimateDate;
	}
	
	private static void defaultStaticExceptionHandler(Exception e) {
		System.err.format("An exception was thrown by thread %s, in class %s, method %s%n" +
				"   type: %s, message: %s%n" +
				"   ", Thread.currentThread().getName(),"SRRegressionResults",
				Thread.currentThread().getStackTrace()[2].getMethodName(),
				e.getClass(),e.getMessage());
	}
	
	@Override
	public boolean isValid() { return validateRegressionResult(); }

	private boolean validateRegressionResult() {
		String problems = "";
		if (regressionMethod==null)
			problems+="The regression method is null.\n";
		if (creationDate==null || creationDate.after(LiteDate.getOrCreate()) || 
				creationDate.before(LiteDate.getOrCreate(2010,(byte)Calendar.JANUARY,(byte)1)))
			problems+="The creation date is invalid/null/impossible.\n";
		if (regressandVariance<=0)
			problems += "The variance is <= 0.\n";
		if (!problems.isEmpty()) {
			System.err.format("While validating the type: %s%n" +
					"   found the following problems: %n%s%n", getClass().getName(),problems);
			return false;
		}
		return true;
	}

	/**
	 * Tells you if the regression is accurate to a certain percent.
	 * @param i
	 * @return
	 */
	public boolean isAccurateTo(int i) {
		double acc = 0;
		try {
			double est = getEstimateValue();
			if (est<0) return false;
			double stdev = getStandardDeviation();
			acc = 100*(est-stdev)/est;
		} catch (Exception e) {
			// does nothing.
		}
		return acc>i;
	}
	
	public boolean isR2GreaterThan(double otherR2) {
		return (coefficientOfDetermination>otherR2);
	}

	public double getCoefficientOfDetermination() {
		return coefficientOfDetermination;
	}

	@Override
	public int compareTo(String o) {
		return 0;
	}

//	@Override
//	public boolean save(boolean multiThread) throws InterruptedException {
//		return false;
//	}
	
	public String seeMath() {
		String s = "";
		double sum = 0;
		for (Indicator indicator : this.items) {
			sum+=indicator.getProduct();
			s+=indicator.getID()+" "+(indicator.isDerivative() ? "(dr)" : "(nd)")
				+": last value="+indicator.getLastValue()+", factor="+
				indicator.getFactor()+", product="+indicator.getProduct()+", sum="+
				sum+"\n";
		}
		return s;
	}
}