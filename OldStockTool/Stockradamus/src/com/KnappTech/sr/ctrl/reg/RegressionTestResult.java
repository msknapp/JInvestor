package com.KnappTech.sr.ctrl.reg;

import org.apache.log4j.Logger;

import com.KnappTech.sr.model.constants.RegressionMethod;
import com.KnappTech.util.MultipleDataPoints;

/**
 * Has details about the result of a regression test.  This is the result
 * of regressing once price history against multiple economic records.
 * @author Michael Knapp
 */
public class RegressionTestResult {
	private static final Logger logger = Logger.getLogger(RegressionTestResult.class);
	private double variance;
	private double r2;
	private double rating;
	private double[] standardErrors;
	private String targetID;
	private String[] indicatorIDs;
	private boolean[] indicatorDerivativeStatus;
	private final RegressionMethod regressionMethod; 
	
	private RegressionTestResult(double variance, double r2,
			double rating, String weakestID,double[] standardErrors,
			String targetID,String[] indicatorIDs,boolean[] derivatives,
			RegressionMethod regressionMethod)
	{
		this.regressionMethod = regressionMethod;
		update(variance,r2,weakestID,standardErrors,targetID,indicatorIDs,derivatives);
	}
	
	private RegressionTestResult(double variance, double r2,
			String weakestID,double[] standardErrors,
			String targetID,String[] indicatorIDs,
			boolean[] derivatives,RegressionMethod regressionMethod)
	{
		this.regressionMethod = regressionMethod;
		update(variance,r2,weakestID,standardErrors,targetID,indicatorIDs,derivatives);
	}
	
	public static final RegressionTestResult create(MultipleDataPoints mdp,
			RegressionMethod regressionMethod) {
		double v = mdp.getVariance();
		return new RegressionTestResult(v,mdp.getCoeffOfDetermination(),
				mdp.getIDofLargestError(), 
				mdp.getErrors(), mdp.getyID(), mdp.getxID(),
				mdp.getDerivatives(),regressionMethod);
	}
	
	public void update(MultipleDataPoints mdp) {
		logger.trace("Started update (from mdp) method, " +
				"going to have multiple data points get the variance.");
		double v = mdp.getVariance();
		logger.trace("Finished having multiple data points get the variance.");
		update( v, mdp.getCoeffOfDetermination(), mdp.getIDofLargestError(), 
				mdp.getErrors(), mdp.getyID(), mdp.getxID(),mdp.getDerivatives());
		logger.trace("Finished update method.");
	}
	
	private void update(double variance, double r2,
			String weakestID,double[] standardErrors,
			String targetID,String[] indicatorIDs,boolean[] derivatives) {
		this.variance = variance;
		this.r2 = r2;
		this.standardErrors = standardErrors;
		this.targetID = targetID;
		this.indicatorIDs = indicatorIDs;
		this.rating = regressionMethod.getRating(variance, standardErrors, r2);
		this.indicatorDerivativeStatus = derivatives;
	}

	public final double getVariance() {
		return variance;
	}

	public final double getR2() {
		return r2;
	}

	public final double getRating() {
		return rating;
	}

	public final IDInfo getWeakestInfo() {
		if (standardErrors.length<1) 
			throw new IllegalStateException("Tried getting the weakest info, but "+
					"the standard errors array is empty.");
		int i=0;
		for (int index = 0;index<standardErrors.length;index++) {
			if (standardErrors[index]>standardErrors[i])
				i=index;
		}
		IDInfo idi = new IDInfo(this.indicatorIDs[i],this.indicatorDerivativeStatus[i],i);
		return idi;
	}

	public final double[] getStandardErrors() {
		return standardErrors;
	}

	public final String getTargetID() {
		return targetID;
	}

	public final String[] getIndicatorIDs() {
		return indicatorIDs;
	}
	
	public static class IDInfo {
		private final String id;
		private final boolean derivative;
		private final int index;
		
		public IDInfo(String id,boolean derivative,int index) {
			this.id = id;
			this.derivative = derivative;
			this.index = index;
		}
		
		public String getID() {
			return id;
		}
		
		public boolean isDerivative() {
			return derivative;
		}
		
		public int getIndex() {
			return index;
		}
	}
}