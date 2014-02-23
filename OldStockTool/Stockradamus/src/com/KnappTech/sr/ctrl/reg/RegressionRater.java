package com.KnappTech.sr.ctrl.reg;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.KnappTech.sr.model.Regressable.Record;
import com.KnappTech.sr.model.Regressable.RecordList;
import com.KnappTech.sr.model.Regressable.RecordSet;
import com.KnappTech.sr.model.comp.Indicator;
import com.KnappTech.sr.model.comp.RegressionResult;
import com.KnappTech.sr.model.constants.RegressionMethod;
import com.KnappTech.util.KTTimer;
import com.KnappTech.util.MultipleDataPoints;

public class RegressionRater {
	private static final Logger logger = Logger.getLogger(RegressionRater.class);
	private final RegressionMethod regressionMethod;
	private final int minimumPoints;
	
	private MultipleDataPoints mdp = null;
	private RegressionTestResult rtr = null;
	private RegressionResult results = null;
	
	public RegressionRater() {
		this.regressionMethod = RegressionMethod.COEFFOFDET;
		this.minimumPoints = 200;
	}
	
	public RegressionRater(RegressionMethod regressionMethod) {
		this.regressionMethod = regressionMethod;
		this.minimumPoints = 200;
	}
	
	private RegressionRater(RegressionMethod regressionMethod,int minimumPoints) {
		this.regressionMethod = regressionMethod;
		this.minimumPoints = minimumPoints;
	}
	
	public static final RegressionRater create(RegressionMethod regressionMethod,int minimumPoints) {
		if (minimumPoints>0) {
			return new RegressionRater(regressionMethod,minimumPoints);
		}
		throw new IllegalArgumentException("Must have a positive minimum number of points.");
	}
	
	public RegressionTestResult getLastRegressionTestResult() {
		return rtr;
	}
	
	public void reset() {
		this.mdp=null;
		this.rtr=null;
		this.results=null;
	}

	public RegressionTestResult evaluate(RecordList<? extends Record> interest, 
			RecordSet<? extends RecordList<? extends Record>> indicators) {
		configure(interest,indicators);
		if (mdp.lengthY()<minimumPoints)
			throw new RuntimeException("Not enough points in the multiple data " +
					"points for a regression.");
		KTTimer timer2 = new KTTimer("performing regression",5,
				"taking very long to perform the regression on "+
				mdp.getyID()+"...",false);
    	if (rtr==null)
    		rtr = RegressionTestResult.create(mdp, regressionMethod);
    	else 
    		rtr.update(mdp);
    	timer2.stop();
    	return rtr;
	}
	
	/**
	 * Tells the rater
	 * @param interest what recordlist you are trying to predict.
	 * @param indicators what record lists might help predict it.
	 */
	private void configure(RecordList<? extends Record> interest, 
			RecordSet<? extends RecordList<? extends Record>> indicators) 
			throws IllegalArgumentException {
		if (interest==null || interest.size()<1 || indicators==null || indicators.size()<1) {
			if (interest==null)
				logger.warn("In configure method, interest was null");
			else if (interest.size()<1)
				logger.warn("In configure method, interest was empty");
			if (indicators==null)
				logger.warn("In configure method, indicators was null");
			else if (indicators.size()<1)
				logger.warn("In configure method, indicators was empty");
			throw new IllegalArgumentException("Given a null or empty interest or indicators.");
		}
		if (indicators.size()>16) {
			logger.warn("In configure method, the indicators had too many records.");
			throw new IllegalArgumentException("Using far too many record lists to make a prediction.");
		}
		if (indicators.getIDSet().size()<indicators.size()) {
			logger.warn("In configure method, there was at least one duplicate record.");
			throw new IllegalArgumentException("There is a duplicate indicator.");
		}
		mdp = indicators.getMultipleDataPoints(interest);
		results = null; // must set to null.
	}
	
	public final RegressionResult getResults(String uid) {
		if (results==null)
			results = createResults(uid);
		return results;
	}
	
	private RegressionResult createResults(String uid) {
		if (mdp==null || rtr==null)
			throw new RuntimeException("Cannot create results before evaluating the regression");
		ArrayList<Indicator> indicators = new ArrayList<Indicator>();
//		System.out.println(mdp.seeMath(4));
		for (int i=0;i<mdp.numberOfInputs();i++) {
			String id = mdp.getxID(i);
			double factor = mdp.getFactor(id);
			double error = mdp.getError(id);
			double lastValue = mdp.getLastValue(id);
			boolean derivative = mdp.getDerivativeStatus(id);
			Indicator indicator = Indicator.create(id, factor, error, (byte)0,derivative);
			indicator.setLastValue(lastValue);
			indicators.add(indicator);
		}
		RegressionResult results = RegressionResult.create(mdp.getVariance(), 
				mdp.getCoeffOfDetermination(), regressionMethod,uid);
		if (results==null) {
			logger.warn("Failed to create results!");
			return null;
		}
        results.setIndicators(indicators);
        results.updateEstimate();
		return results;
	}

	public RegressionMethod getRegressionMethod() {
		return regressionMethod;
	}
}