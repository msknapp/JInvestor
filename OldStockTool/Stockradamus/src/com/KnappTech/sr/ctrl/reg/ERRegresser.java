package com.KnappTech.sr.ctrl.reg;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.math.linear.SingularMatrixException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.ctrl.reg.RegressionTestResult.IDInfo;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.EconomicRecords;
import com.KnappTech.sr.model.Regressable.FilterResponse;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.Regressable.Record;
import com.KnappTech.sr.model.Regressable.RecordFilter;
import com.KnappTech.sr.model.Regressable.RecordList;
import com.KnappTech.sr.model.Regressable.RecordSet;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.comp.RegressionResult;
import com.KnappTech.sr.model.constants.RegressionMethod;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.util.MethodResponse;

@Deprecated
public class ERRegresser {
	private static final long serialVersionUID = 201003061051L;
	private static final Logger logger = Logger.getLogger(ERRegresser.class);
	
	public static final String TIMESTAMP = LiteDate.getOrCreate().getFormatted("yyMMdd");
	
	/**
	 * The company we are forming a prediction for.
	 */
	private Company company = null;
	
	private PriceHistory history = null;
	
	/**
	 * The set of economic records we are using to make a prediction.  This 
	 * is the set whose size must not exceed the max number of indicators.
	 */
	private EconomicRecords predictingEconomicRecords = null;
	private final RegressionRater regressionRater;
	private RegressionTestResult regressionTestResult = null;
	private ERFilter filter = null;
	private double bestRating = Double.MIN_VALUE;
	private double lastRating = 0;
	private short economicRecordsConsidered = 0;
	private short regressionsPerformed = 0;
	
	public ERRegresser() {
		this.regressionRater = new RegressionRater(RegressionMethod.COEFFOFDET);
	}
	
	public ERRegresser(RegressionMethod regressionMethod) {
		this.regressionRater = new RegressionRater(regressionMethod);
	}
	
	private ERRegresser(RegressionMethod regressionMethod,int minimumPoints) {
		this.regressionRater = RegressionRater.create(regressionMethod, minimumPoints);
	}
	
	public static final ERRegresser create(RegressionMethod regressionMethod,int minimumPoints) {
		if (minimumPoints>0)
			return new ERRegresser(regressionMethod,minimumPoints);
		IllegalArgumentException e = new IllegalArgumentException(
				"must have a positive number of minimum points.");
		throw e;
	}
	
	private void initialize() {
		economicRecordsConsidered=0;
		regressionsPerformed=0;
		bestRating = -Double.MAX_VALUE;
		lastRating = 0;
		history=null;
		regressionTestResult = null;
		regressionRater.reset();
	}
	
	public void executeRegression(Company company) throws InterruptedException {
//		if (logger.getEffectiveLevel().equals(Level.TRACE))
//			logger.trace("Starting to execute regression on company: "+company.getID());
//		initialize();
//		if (company==null)
//			throw new NullPointerException("Cannot execute a regression on a null company.");
//		this.company = company;
//		history = loadPriceHistoryIfNecessary(company);
//		if (Thread.interrupted())
//			throw new InterruptedException();
//		if (history==null)
//			throw new RuntimeException("Failed to get the price history for the company, "+
//					"so the regression cannot be performed.");
//		if (history.getEndDate().before(LiteDate.getOrCreate(Calendar.MONTH, -2))) {
//			logger.warn("Not going to regress "+history.getID()+" because the "+
//					"price history does not reach the minimum end date.  You might "+
//					"need to update price histories, or this company may have gone " +
//					"out of business.");
//			return;
//		}
//		EconomicRecords allLoadedEconomicRecords;
//		try {
//			allLoadedEconomicRecords = (EconomicRecords) PersistenceRegister.er().getAllThatAreLoaded();
//			if (allLoadedEconomicRecords.size()>2500) {
//				logger.warn("Warning: there are "+allLoadedEconomicRecords.size()+" records to consider, but the regression " +
//						"finder loop escapes at 1000");
//			}
//			if (history.size()<=240)
//				return;
//			lastRating = -(Double.MAX_VALUE/1000);
//			long startTime = System.currentTimeMillis();
//			logger.info("Starting regression find for "+company.getID());
//			LiteDate maxStartDateForIndicators = calculateMaxStartDateForIndicators(history);
//			LiteDate minEndDateForIndicators = (LiteDate.getOrCreate()).addMonths(-3);
//			if (!logger.getEffectiveLevel().isGreaterOrEqual(Level.INFO))
//				logger.debug("-- initializing predictingEconomicRecords");
//			predictingEconomicRecords = new EconomicRecords();
//			filter = new ERFilter(predictingEconomicRecords, maxStartDateForIndicators, 
//					minEndDateForIndicators, 240);
//			if (Thread.interrupted())
//				throw new InterruptedException();
//			predictingEconomicRecords = allLoadedEconomicRecords.subSet(filter,MAXINDICATORS);
//			if (Thread.interrupted())
//				throw new InterruptedException();
//			predictingEconomicRecords.setMaximumSize(MAXINDICATORS);
////			logger.trace("-- predictingEconomicRecords is initialized:\n\t"+predictingEconomicRecords);
//			filter.setMinimumRealValues(240);
//			if (Thread.interrupted())
//				throw new InterruptedException();
//			regressAgainstRecords(allLoadedEconomicRecords);
//			if (Thread.interrupted())
//				throw new InterruptedException();
//			if (isConsiderDerivatives())
//				regressAgainstDerivativeRecords(allLoadedEconomicRecords);
//			if (Thread.interrupted())
//				throw new InterruptedException();
//			if (!logger.getEffectiveLevel().isGreaterOrEqual(Level.INFO))
//				logger.debug("Finished regressions against all indicators, " +
//					"best rating so far: "+bestRating+"\nnow reducing the indicator set.");
//			if (economicRecordsConsidered>2200) {
//				logger.warn("Warning: ER Regresser loop seems to go forever, count of iterations = "+economicRecordsConsidered+
//						", the loop escapes at 2500, records size = "+allLoadedEconomicRecords.size());
//			}
//			
//			if (predictingEconomicRecords.size()<1) {
//				logger.warn("Warning: the regresser did not find even one record to regress against.");
//			} else if (predictingEconomicRecords.size()<MAXINDICATORS) {
//				logger.warn("Warning: the regresser did not find the minimum of 16 records to regress against.");
//			}
//			reduceRecords();
//			if (Thread.interrupted())
//				throw new InterruptedException();
////			predictingEconomicRecords.incrementTimesUsedInRegressionResult();
//			runRegressionNow();
//			if (Thread.interrupted())
//				throw new InterruptedException();
//			String uid = company.getID()+TIMESTAMP;
//			RegressionResult r = regressionRater.getResults(uid);
//			company.addRegressionResults(r);
//			long endTime = System.currentTimeMillis();
//			double duration = Math.round((endTime-startTime)/100)/10;
////			if (!logger.getEffectiveLevel().isGreaterOrEqual(Level.INFO))
//				logger.info("Regression took "+duration+" seconds.");
//			double lv = history.getLastValue();
//			if (bestRating>0) {
//				String msg = "Finished regressing "+history.getID()+", best rating: "+bestRating+", st dev: "+
//				r.getStandardDeviation()+", History Last Value: "+lv+", estimate: "+r.getUpToDateEstimate();
//				logger.info(msg);
////				System.out.format("Finished regressing %s, best rating: %.2f, st dev: %.2f, History Last Value: %.2f, estimate: %.2f%n",
////					history.getID(), bestRating, r.getStandardDeviation(), lv, r.getUpToDateEstimate());
//			} else {
//				logger.warn("Regression finding algorithm failed for "+company.getID());
//			}
////			logger.info(r.seeMath());
//			if (this.regressionsPerformed<15)
//				System.err.println("Performed less than 15 regressions for "+company.getID());
//			logger.info("Finished regression find for "+company.getID());
//		} catch (Exception e1) {
//			if (e1 instanceof InterruptedException)
//				throw (InterruptedException)e1;
//			logger.error("Unhandled exception in ERRegresser, the regression is stopping for "+history.getID(),e1);
//			e1.printStackTrace();
//		} finally {
//			PersistenceRegister.ph().remove(history.getID());
//			if (logger.getEffectiveLevel().equals(Level.TRACE))
//				logger.trace("Finished executing regression on company: "+company.getID());
//		}
	}
	
	private void reduceRecords() {
		if (!logger.getEffectiveLevel().isGreaterOrEqual(Level.INFO))
			logger.debug("About to remove indicators while the score improves.");
		// remove indicators while the score increases for that.
		boolean scoreIsWorse = false;
		IDInfo weakestInfo;
		EconomicRecord lastRemovedRecord = null;
		double rating = regressionRater.getRegressionMethod().getWorstPossibleRating();
		while (!scoreIsWorse && predictingEconomicRecords.size()>1) {
			try {
				regressionTestResult = regressionRater.evaluate(history,predictingEconomicRecords);
				rating = regressionTestResult.getRating();
				scoreIsWorse = regressionRater.getRegressionMethod().isFirstScoreWorse(rating,bestRating);
				if (scoreIsWorse) { // undo last remove:
					if (lastRemovedRecord==null)
						continue;
					try {
						if (logger.getEffectiveLevel().equals(Level.TRACE))
							logger.trace("-- going to add to predictingEconomicRecords ("+predictingEconomicRecords.size()+"): "
								+lastRemovedRecord.getID()+", "+String.valueOf(lastRemovedRecord.isDerivative()));
						predictingEconomicRecords.add(lastRemovedRecord);
					} catch (IllegalArgumentException e) {}
					runRegressionNow();
				} else { // score is better, try removing another.
					bestRating = rating;
					weakestInfo = regressionTestResult.getWeakestInfo();
					lastRemovedRecord = predictingEconomicRecords.get(weakestInfo.getID(),weakestInfo.isDerivative());
					if (logger.getEffectiveLevel().equals(Level.TRACE))
						logger.trace("-- going to remove from predictingEconomicRecords ("+predictingEconomicRecords.size()+"): "
							+weakestInfo.getID()+", "+String.valueOf(weakestInfo.isDerivative()));
					predictingEconomicRecords.remove(weakestInfo.getID(),weakestInfo.isDerivative());
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Warning: uncaught exception in the er regresser while loop (875325)",e);
			}
		}
	}
	
	private void regressAgainstRecords(EconomicRecords allPossibleRecords) throws InterruptedException {
//		double bestRating = 0;
//		int consecutiveTimesAtScore = 0;
//		List<EconomicRecord> items = allPossibleRecords.getRecords();
//		int i = 0;
//		if (!logger.getEffectiveLevel().isGreaterOrEqual(Level.INFO))
//			logger.debug("=== Starting to regress against all given non-derivative records. ===");
//		for (EconomicRecord record : items) {
//			i++;
//			// step 1, consider normal record.
//			regressAgainstRecord(record);
//			double newRating = -1;
//			if (regressionTestResult!=null)
//				newRating = regressionTestResult.getRating();
//			if (newRating>bestRating) {
//				consecutiveTimesAtScore=0;
//				bestRating = newRating;
//			} else
//				consecutiveTimesAtScore++;
//			if (bestRating>getEarlyTerminationScore()) {
//				if (!logger.getEffectiveLevel().isGreaterOrEqual(Level.INFO))
//					logger.debug("   Stopping regression because we have reached 92% predictability.");
//				break;
//			}
//			if (consecutiveTimesAtScore>100) {
//				if (!logger.getEffectiveLevel().isGreaterOrEqual(Level.INFO))
//					logger.debug("   Stopping regression because we have had " +
//						"the same score 100 times in a row. have considered "+i+" items.");
//				break;
//			}
//		}
//		if (!logger.getEffectiveLevel().isGreaterOrEqual(Level.INFO))
//			logger.debug("=== Finished regressing against all given records. ===");
	}
	
	private void regressAgainstDerivativeRecords(EconomicRecords allPossibleRecords) throws InterruptedException {
//		double bestRating = 0;
//		int consecutiveTimesAtScore = 0;
//		List<EconomicRecord> items = allPossibleRecords.getRecords();
//		int i = 0;
//		if (!logger.getEffectiveLevel().isGreaterOrEqual(Level.INFO))
//			logger.debug("=== Starting to regress against all given derivative records. ===");
//		for (EconomicRecord record : items) {
//			i++;
//			double newRating = -1;
//			// step 2, now consider the derivative record.
//			regressAgainstRecord((EconomicRecord) record.getDerivative());
//			if (regressionTestResult!=null)
//				newRating = regressionTestResult.getRating();
//			if (newRating>bestRating) {
//				consecutiveTimesAtScore=0;
//				bestRating = newRating;
//			} else
//				consecutiveTimesAtScore++;
//			if (bestRating>getEarlyTerminationScore()) {
//				if (!logger.getEffectiveLevel().isGreaterOrEqual(Level.INFO))
//					logger.debug("   Stopping regression because we have reached 92% predictability.");
//				break;
//			}
//			if (consecutiveTimesAtScore>100) {
//				if (!logger.getEffectiveLevel().isGreaterOrEqual(Level.INFO))
//					logger.debug("   Stopping regression because we have had " +
//						"the same score 200 times in a row. have considered "+i+" items.");
//				break;
//			}
//		}
//		if (!logger.getEffectiveLevel().isGreaterOrEqual(Level.INFO))
//			logger.debug("=== Finished regressing against all given derivative records. ===");
	}
	
	private void regressAgainstRecord(EconomicRecord currentRecord) throws InterruptedException {
//		if (currentRecord==null)
//			throw new NullPointerException("current record given is null.");
//		if (logger.getEffectiveLevel().equals(Level.TRACE))
//			logger.trace("Going to do a regression against record: "+currentRecord.getID());
//		if (Thread.interrupted())
//			throw new InterruptedException();
//		RegressionTestResult oldRes = this.regressionTestResult;
//		EconomicRecord removedRecord = null;
//		try {
//			FilterResponseImpl fr = filter.acceptable(currentRecord);
//			if (!fr.pass()) { // these records are not acceptable.
//				if (logger.getEffectiveLevel().equals(Level.TRACE)) {
//					logger.trace("The record "+currentRecord.getID()+", derivative="+
//						String.valueOf(currentRecord.isDerivative())+
//						", did not pass the filter." +
//						"\n\t"+fr.toString());
//				}
//				economicRecordsConsidered++;
//				return;
//			}
//			if (predictingEconomicRecords.size()>=MAXINDICATORS)
//				removedRecord=removeWeakestIndicator();
//			if (logger.getEffectiveLevel().equals(Level.TRACE))
//				logger.trace("-- Going to add if not a duplicate to predictingEconomicRecords ("+predictingEconomicRecords.size()+"): "+
//					currentRecord.getID()+", "+currentRecord.isDerivative());
//			MethodResponse m = predictingEconomicRecords.addIfNotDuplicate(
//					(EconomicRecord)currentRecord);
//			if (!m.isPass()) {
//				if (!m.getComment().startsWith("dup")) // for duplicate record.  I don't care.
//					logger.warn("Warning: Tried adding a record to the indicators, " +
//						"but it was refused.\n\t"+m.getComment());
//				economicRecordsConsidered++;
//				return;
//			}
//			if (predictingEconomicRecords.size()>MAXINDICATORS)
//				logger.warn("Expecting "+MAXINDICATORS+" in adjusted indicators, " +
//						"but have "+predictingEconomicRecords.size());
//			runRegressionNow();
//			regressionsPerformed++;
//			if (regressionsPerformed%50==0) {
//				String msg = "For "+company.getID()+" we have considered "+economicRecordsConsidered+" economic records and performed "+
//				regressionsPerformed+" regressions, the best rating is "+bestRating;
//				if (!logger.getEffectiveLevel().isGreaterOrEqual(Level.INFO))
//					logger.debug(msg);
//			}
//			double rating = regressionTestResult.getRating();
//			if (predictingEconomicRecords.size()<MAXINDICATORS)
//				return;
//			// if the new rating is worse than the last rating...
//			if (regressionRater.getRegressionMethod().isFirstScoreBetter(lastRating, rating))
//			{	// ... then you should remove the record that was just added.
//				if (logger.getEffectiveLevel().equals(Level.TRACE))
//					logger.trace("-- Going to remove from predictingEconomicRecords ("+predictingEconomicRecords.size()+"): "+
//						currentRecord.getID()+", "+currentRecord.isDerivative());
//				predictingEconomicRecords.remove(currentRecord.getID(),currentRecord.isDerivative());
//				if (removedRecord!=null) {
//					if (logger.getEffectiveLevel().equals(Level.TRACE))
//						logger.trace("-- Going to add to predictingEconomicRecords ("+predictingEconomicRecords.size()+"): "+
//							removedRecord.getID()+", "+removedRecord.isDerivative());
//					predictingEconomicRecords.add(removedRecord);
//					regressionTestResult=oldRes;
//				} else {
//					runRegressionNow();
//				}
//			}
//			bestRating = regressionRater.getRegressionMethod().getTheBetterScore(rating, bestRating);
//			lastRating=rating;
//		} catch (SingularMatrixException e) {
//			logger.error("Encountered a singular matrix exception.",e);
//			if (logger.getEffectiveLevel().equals(Level.TRACE))
//				logger.trace("-- Going to remove from predictingEconomicRecords ("+predictingEconomicRecords.size()+"): "+
//					currentRecord.getID()+", "+String.valueOf(currentRecord.isDerivative()));
//			predictingEconomicRecords.remove(currentRecord.getID(),currentRecord.isDerivative());
//			if (removedRecord!=null) {
//				if (logger.getEffectiveLevel().equals(Level.TRACE))
//					logger.trace("-- Going to add to predictingEconomicRecords ("+predictingEconomicRecords.size()+"): "+
//						removedRecord.getID()+", "+removedRecord.isDerivative());
//				predictingEconomicRecords.add(removedRecord);
//				regressionTestResult=oldRes;
//			} else {
//				runRegressionNow();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("Warning: uncaught exception in er regresser (2587150)",e);
//			String msg = "Price History: "+history.getID()+", "+history.getStartDate()+" - "+history.getEndDate();
//			for (EconomicRecord er : this.predictingEconomicRecords.getItems())
//				msg+="\n"+er.getID()+": "+er.getStartDate()+" - "+er.getEndDate()+", size="+er.size();
//			logger.error(msg);
//		} finally {
//			if (logger.getEffectiveLevel().equals(Level.TRACE))
//				logger.trace("Finished the regression against record: "+currentRecord.getID());
//		}
	}
	
	private EconomicRecord removeWeakestIndicator() {
		if (regressionTestResult==null)
			return removeSmallestIndicator();
		try {
			IDInfo weakestInfo = regressionTestResult.getWeakestInfo();
			EconomicRecord r = predictingEconomicRecords.get(weakestInfo.getID(),weakestInfo.isDerivative());
			if (logger.getEffectiveLevel().equals(Level.TRACE))
				logger.trace("-- Going to remove from predictingEconomicRecords ("+predictingEconomicRecords.size()+"): "+
					weakestInfo.getID()+", "+String.valueOf(weakestInfo.isDerivative()));
			if (!predictingEconomicRecords.remove(weakestInfo.getID(),weakestInfo.isDerivative())) {
				if (predictingEconomicRecords.size()>0) {
					if (r!=null)
						logger.warn("Failed to remove weakest id: "+weakestInfo.getID()
							+", derivative="+String.valueOf(weakestInfo.isDerivative())+
							", found="+String.valueOf(r!=null));
					r = removeSmallestIndicator();
				}
			}
			return r;
		} catch (IllegalStateException e) {
			logger.warn("Weakest id could not be determined.");
			if (logger.getEffectiveLevel().equals(Level.TRACE)) {
				EconomicRecord er = predictingEconomicRecords.getSmallestRecord();
				logger.trace("-- Going to remove smallest record from predictingEconomicRecords ("+predictingEconomicRecords.size()+"): "+
					er.getID()+", "+String.valueOf(er.isDerivative()));
			}
			boolean b = predictingEconomicRecords.removeSmallestRecord();
		}
		return null;
	}
	
	private EconomicRecord removeSmallestIndicator() {
		EconomicRecord r = predictingEconomicRecords.getSmallestRecord();
		if (logger.getEffectiveLevel().equals(Level.TRACE))
			logger.trace("-- Going to remove smallest record from predictingEconomicRecords ("+predictingEconomicRecords.size()+"): "+
				r.getID()+", "+String.valueOf(r.isDerivative()));
		boolean b = predictingEconomicRecords.removeSmallestRecord();
		if (b)
			return r;
		logger.warn("Cannot seem to reduce the size of " +
				"these economic records");
		return null;
	}
	
	private void runRegressionNow() {
		if (logger.getEffectiveLevel().equals(Level.TRACE))
			logger.trace("Having regression rater do another evaluation.");
		regressionTestResult = regressionRater.evaluate(history, predictingEconomicRecords);
		if (logger.getEffectiveLevel().equals(Level.TRACE))
			logger.trace("Finished having regression rater do another evaluation.");
	}
	
	public class SimpleERFilter implements RecordFilter {
		private LiteDate maxStartDateForIndicators = null;
		private LiteDate minEndDateForIndicators = null;
		private int minimumRealValues = 300;
		public SimpleERFilter(LiteDate maxStartDateForIndicators,
				LiteDate minEndDateForIndicators,
				int minimumRealValues) {
			this.maxStartDateForIndicators = maxStartDateForIndicators;
			this.minEndDateForIndicators = minEndDateForIndicators;
			this.minimumRealValues = minimumRealValues;
		}
		
		@Override
		public FilterResponseImpl acceptable(RecordList<? extends Record> record) {
			boolean b0 = (record!=null && 
					record.getStartDate()!=null &&
					record.getEndDate()!=null);
			if (!b0)
				return new FilterResponseImpl(false,b0,true,true,true,true,null);
			boolean b1 = !record.getStartDate().after(maxStartDateForIndicators);
			boolean b2 = !record.getEndDate().before(minEndDateForIndicators);
			boolean b4 = record.size()>minimumRealValues;
			boolean pass = (b0 && b1 && b2 && b4);
			return new FilterResponseImpl(pass,b0,b1,b2,true,b4,null);
		}
		
		public void setMinimumRealValues(int minReal) {
			this.minimumRealValues = minReal;
		}

		@Override
		public RecordSet<? extends RecordList<? extends Record>> getCurrentRecords() {
			return null;
		}

		@Override
		public void setCurrentRecords(
				RecordSet<? extends RecordList<? extends Record>> records) {
			// do nothing.
		}
	}
	
	public static class ERFilter implements RecordFilter {
		private LiteDate maxStartDateForIndicators = null;
		private LiteDate minEndDateForIndicators = null;
		private EconomicRecords adjustedIndicators = null;
		private int minimumRealValues = 300;
		
		public ERFilter(EconomicRecords adjustedIndicators,
				LiteDate maxStartDateForIndicators,LiteDate minEndDateForIndicators,
				int minimumRealValues) {
			this.adjustedIndicators = adjustedIndicators;
			this.maxStartDateForIndicators = maxStartDateForIndicators;
			this.minEndDateForIndicators = minEndDateForIndicators;
			this.minimumRealValues = minimumRealValues;
		}
		
		@Override
		public FilterResponseImpl acceptable(RecordList<? extends Record> record) {
			boolean b0 = (record!=null && 
					record.getStartDate()!=null &&
					record.getEndDate()!=null);
			if (!b0)
				return new FilterResponseImpl(false,false,true,true,true,true,null);
			boolean b1 = record.getStartDate().onOrBefore(maxStartDateForIndicators);
			boolean b2 = record.getEndDate().onOrAfter(minEndDateForIndicators);
			MethodResponse m3 = adjustedIndicators.hasSimilarList((EconomicRecord)record);
			boolean b3 = !m3.isPass();
			boolean b4 = record.size()>minimumRealValues;
			boolean pass = (b0 && b1 && b2 && b3 && b4);
			return new FilterResponseImpl(pass,b0,b1,b2,b3,b4,m3);
		}
		
		public void setMinimumRealValues(int minReal) {
			this.minimumRealValues = minReal;
		}

		@Override
		public EconomicRecords getCurrentRecords() {
			return adjustedIndicators;
		}

		@Override
		public void setCurrentRecords(
				RecordSet<? extends RecordList<? extends Record>> records) {
			this.adjustedIndicators = (EconomicRecords)records;
		}
	}
	
	private static class FilterResponseImpl implements FilterResponse {
		private final boolean pass;
		private final boolean validRecord;
		private final boolean acceptableStartDate;
		private final boolean acceptableEndDate;
		private final boolean notDuplicate;
		private final boolean enoughRecords;
		private final MethodResponse methodResponse;
		
		public FilterResponseImpl(boolean pass, boolean validRecord,
				boolean acceptableStartDate,boolean acceptableEndDate,
				boolean notDuplicate,boolean enoughRecords,
				MethodResponse m) {
			this.pass = pass;
			this.validRecord = validRecord;
			this.acceptableStartDate = acceptableStartDate;
			this.acceptableEndDate = acceptableEndDate;
			this.notDuplicate = notDuplicate;
			this.enoughRecords = enoughRecords;
			this.methodResponse = m;
		}
		
		public final boolean pass() {
			return pass;
		}
		
		public String toString() {
			if (pass)
				return "This record is acceptable.";
			String s = "The record failed because: \n";
			if (!validRecord) {
				s+="the record had null values.";
				return s;
			}
			if (!acceptableStartDate) {
				s+="\tthe start date was too recent.\n";
			}
			if (!acceptableEndDate) {
				s+="\tthe end date was too recent.\n";
			}
			if (!notDuplicate) {
				s+="\tthe record appears to be a duplicate.\n";
			}
			if (!enoughRecords) {
				s+="\tthe record does not have enough entries.\n";
			}
			if (methodResponse!=null) {
				s+="\t"+methodResponse.getComment();
			}
			return s;
		}

		public MethodResponse getMethodResponse() {
			return methodResponse;
		}
	}

	public RegressionMethod getRegressionMethod() {
		return regressionRater.getRegressionMethod();
	}
	
	private static final LiteDate calculateMaxStartDateForIndicators(RecordList<Record> history) {
		LiteDate startPlusTwo = history.getStartDate().addYears(2);
		LiteDate twelveYearsAgo = history.getEndDate().addYears(-12);
		if (startPlusTwo.after(twelveYearsAgo)) {
			return startPlusTwo;
		} else {
			return twelveYearsAgo;
		}
	}
}