package com.KnappTech.sr.model.Regressable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.KnappTech.model.LiteDate;
import com.KnappTech.model.TimeFrame;
import com.KnappTech.model.UpdateFrequency;
import com.KnappTech.sr.model.AbstractSKTSet;
import com.KnappTech.sr.model.comp.Indicator;
import com.KnappTech.util.MethodResponse;
import com.KnappTech.util.MultipleDataPoints;

public abstract class RecordSet<REGTYPE extends RecordList<? extends Record>> 
extends AbstractSKTSet<REGTYPE> {
	private static final long serialVersionUID = 201010091451L;
	
	public RecordSet() {
		
	}
	
	public RecordSet(Collection<REGTYPE> c) {
		addAll(c);
	}
	
	/**
	 * Adjusts all of the sub items to the frequency provided.
	 * @param uf
	 */
	public void adjustForFrequency(UpdateFrequency uf) {
//		logger.trace("Started adjusting Frequency");
		for (REGTYPE lis : items) {
			if (lis!=null && !lis.getUpdateFrequency().equals(uf)) {
				lis.adjustToFrequency(uf);
			}
		}
//		logger.trace("Finished adjusting Frequency");
	}
	
	/**
	 * Builds a matrix of doubles using the common valid indexes provided.
	 * @param commonValidIndexes
	 * @return
	 */
	public double[][] getX(LinkedHashSet<Integer> commonValidIndexes) {
//		logger.trace("Started get X");
		int i = 0;
		double[][] x = new double[commonValidIndexes.size()][this.size()];
		Iterator<Integer> iter = commonValidIndexes.iterator();
		while (iter.hasNext()) {
			int ind = iter.next();
			int j = 0;
			for (REGTYPE lis : items) {
				x[i][j] = lis.getValueAtIndex(ind);
				j++;
			}
			i++;
		}
//		logger.trace("Finished get X");
		return x;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public RecordSet<REGTYPE> clone() {
//		logger.trace("Creating a clone");
		RecordSet<REGTYPE> clone = createInstance();
		for (REGTYPE lis : items) {
			REGTYPE listClone = (REGTYPE) lis.clone();
			clone.add(listClone);
		}
//		logger.trace("Finished creating a clone");
		return clone;
	}
	
	/**
	 * Determines if we have this id in the list.
	 * @param id
	 * @return
	 */
	public boolean contains(String id) {
		return (get(id)!=null);
	}
	
//	@Override
//	public Report<?> getReport(Filter<Object> instructions) {
//		Report<REGTYPE> report = new Report<REGTYPE>("Record_Set");
//		ReportRow<REGTYPE> row;
//		for (REGTYPE lis : items) {
//			row = report.put(lis);
//			lis.updateReportRow(instructions, row);
//		}
//		return report;
//	}
	
	public MethodResponse addIfNotDuplicate(REGTYPE newList) {
		return addIfNotDuplicate(newList, false);
	}
	
	/**
	 * Adds a record if and only if we do not currently have
	 * one with the same id, or one with remarkably similar
	 * values.  Similarity is assumed when the ratio of values
	 * between the two lists is almost perfectly constant.
	 * @param newList
	 * @return
	 */
	public MethodResponse addIfNotDuplicate(REGTYPE newList,boolean warnIfExceedsSizeLimit) {
//		logger.trace("Trying to add a record if it is not a duplicate.");
		MethodResponse m = null;
		try {
			if (size()<getMaximumSize()) {
				m = hasSimilarList(newList);
				if (!m.isPass()) {
					boolean b= add(newList);
					m =new MethodResponse(b,null,null,(b ? null : "duplicate record"));
					return m;
				}
				m =new MethodResponse(false,m.getResponseArgument(),m.getResponseArguments(),
						"there is a similar list\n\t"+m.getComment());
				return m;
			} else if (warnIfExceedsSizeLimit) {
				String message = "Tried adding an item that would " +
						"make this exceed size limits.";
//				logger.debug(message);
				throw new IllegalStateException(message);
			}
			return new MethodResponse(false,null,null,"exceeds size limit.");
		} finally {
//			logger.trace("Finished attempt to add a record if it is not a duplicate.  response: "+m);
		}
	}
	
	/**
	 * Determines if any of the lists this set has are very similar
	 * to the list provided.  Using this function can help prevent 
	 * a singular matrix exception.  Some lists will have different
	 * ids but all the numbers are a multiple of each other.  This 
	 * could produce inaccurate regressions or runtime exceptions.
	 * You can prevent that by checking with this function.
	 * <p>This starts by seeing if we have a list with the same id
	 * already.  If not, then it will check for lists with a constant
	 * ratio of values.</p>
	 * @param newList
	 * @return
	 */
	public MethodResponse hasSimilarList(REGTYPE newList) {
//		logger.trace("Checking if we have a similar list.");
		MethodResponse m=null;
		try {
			for (REGTYPE lis : items) {
				if (lis.getID().equals(newList.getID()) && 
						lis.isDerivative()==newList.isDerivative()) {
					return new MethodResponse(true,lis,null,"apparent duplicate of "+
							lis.getID()+", "+String.valueOf(lis.isDerivative()));
				}
				m = lis.valuesHaveConstantRatio(newList.getRecords());
				if (m.isPass()) {
					m = new MethodResponse(true,m.getResponseArgument(),m.getResponseArguments(),
							m.getComment()+" duplicate item: "+lis.getID()+", "+
							String.valueOf(lis.isDerivative()));
					return m;
				}
			}
			m = new MethodResponse(false,null,null,"there were no items in the list.");
			return m;
		} finally {
//			logger.trace("Finished checking for a similar list. Response: "+m);
		}
	}
	
	/**
	 * Converts this into a list of indicators with the factors and
	 * errors provided.  The order of the factors and errors must
	 * match the order of the items in this list or you will get
	 * inaccurate data, maybe even see ridiculous predictions.
	 * @param factors
	 * @param errors
	 * @return
	 */
	public ArrayList<Indicator> getIndicators(double[] factors, double[] errors) {
//		logger.trace("Getting indicators");
		ArrayList<Indicator> indicators = new ArrayList<Indicator>();
		if (size()==factors.length && factors.length==errors.length) {
			int i = 0;
			for (RecordList<? extends Record> records : items) {
				Indicator indicator = Indicator.create(records,factors[i],errors[i]);
				indicators.add(indicator);
				i++;
			}
//			logger.trace("Found valid indicators");
			return indicators;
		}
//		logger.debug("Failed to get valid indicators");
		return null;
	}
	
	@Override
	public String toString() {
		String str = "";
		if (!isEmpty()) {
			for (REGTYPE lis : items) {
				str += lis.toString()+"\n";
			}
		} else {
			str = "I am empty";
		}
		return str;
	}

	/**
	 * Comparing a record set does not make sense, sorting this way will
	 * accomplish nothing.
	 */
	@Override
	public int compareTo(String arg0) {
		return 0;
	}
	
	/**
	 * Checks the records it already has for any two that are remarkably
	 * similar based on the ratio of their values being almost perfectly
	 * constant.  If it finds those two, it will return the last one
	 * it found so you can do something with it.  If it does not find
	 * one, this returns null meaning it should not produce a singular
	 * matrix exception when regressed.
	 * @return
	 */
	public RecordList<?> checkForSingularMatrix() {
//		logger.trace("Checking for a singular matrix.");
		ArrayList<REGTYPE> arl = new ArrayList<REGTYPE>(items);
		REGTYPE rl1;
		REGTYPE rl2;
		for (int i = 0;i<arl.size();i++) {
			rl1 = arl.get(i);
			for (int j = i+1;j<arl.size();j++) {
				rl2 = arl.get(j);
				if (rl1.valuesHaveConstantRatio(rl2.getRecords()).isPass()) {
//					logger.trace("Returning a value in check for singular matrix.");
					return rl2;
				}
			}
		}
//		logger.trace("Returning null in check for singular matrix.");
		return null;
	}
	
	/**
	 * Finds the record that is updated the least frequently.
	 * @return
	 */
	public REGTYPE getLeastFrequentlyUpdatedRecord() {
//		logger.trace("Searching for the least frequently updated record.");
		UpdateFrequency leastFrequent = null;
		REGTYPE leastFrequentRecords = null;
		for (REGTYPE records : items) {
			UpdateFrequency uf = records.determineUpdateFrequency();
			if (leastFrequent == null || uf.isLessFrequent(leastFrequent)) {
				leastFrequent = uf;
				leastFrequentRecords = records;
			}
		}
//		logger.trace("Found the least frequently updated record.");
		return leastFrequentRecords;
	}
	
	/**
	 * Finds the record that is updated the most frequently.
	 * @return
	 */
	public REGTYPE getMostFrequentlyUpdatedRecord() {
		UpdateFrequency mostFrequent = null;
		REGTYPE mostFrequentRecords = null;
		for (REGTYPE records : items) {
			UpdateFrequency uf = records.determineUpdateFrequency();
			if (mostFrequent == null || uf.isMoreFrequent(mostFrequent)) {
				mostFrequent = uf;
				mostFrequentRecords = records;
			}
		}
		return mostFrequentRecords;
	}
	
	/**
	 * Finds the record with the least number of valid entries,
	 * where a valid entry must have a non-null date and a value.
	 * @return
	 */
	public REGTYPE getSmallestRecord() {
//		logger.trace("Searching for the smallest record.");
		REGTYPE smallestRecords = null;
		int smallSize = Integer.MAX_VALUE;
		for (REGTYPE records : items) {
			int sz = records.size();
			if (sz<smallSize) {
				smallestRecords = records;
				smallSize = sz;
			}
		}
//		logger.trace("Found the smallest record.");
		return smallestRecords;
	}
	
	/**
	 * Finds the record with the least number of valid entries,
	 * where a valid entry must have a non-null date and a value.
	 * @return
	 */
	public REGTYPE getLargestRecord() {
		REGTYPE largestRecords = null;
		int largeSize = -1;
		for (REGTYPE records : items) {
			int sz = records.size();
			if (sz>largeSize) {
				largestRecords = records;
				largeSize = sz;
			}
		}
		return largestRecords;
	}
	
	/**
	 * Replaces the least frequently updated record with the one provided, if
	 * and only if the one provided is updated more frequently.  This returns
	 * null if nothing is replaced.
	 * @param records
	 * @return
	 */
	public REGTYPE replaceLeastFrequentlyUpdatedRecord(REGTYPE records) {
//		logger.trace("Trying to replace the least frequently updated record.");
		if (records!=null) {
			REGTYPE leastFrequentlyUpdatedRecord = getLeastFrequentlyUpdatedRecord();
			if (leastFrequentlyUpdatedRecord!=null && 
					records.determineUpdateFrequency().isMoreFrequent(
							leastFrequentlyUpdatedRecord.determineUpdateFrequency())) {
				remove(leastFrequentlyUpdatedRecord.getID());
				this.add(records);
//				logger.trace("Finished trying to replace the least frequently updated record.");
				return leastFrequentlyUpdatedRecord;
			}
		}
//		logger.trace("Finished trying to replace the least frequently updated record, returning null.");
		return null;
	}
	
	/**
	 * Replaces the smallest record with the one provided if and only if the one
	 * provided has more valid entries.  This returns null if nothing is replaced.
	 * @param records
	 * @return
	 */
	public REGTYPE replaceSmallestRecord(REGTYPE records) {
		if (records!=null) {
			REGTYPE smallestRecord = getSmallestRecord();
			if (smallestRecord!=null && 
					records.size()>smallestRecord.size()) {
				remove(smallestRecord.getID());
				this.add(records);
				return smallestRecord;
			}
		}
		return null;
	}
	
	/**
	 * Removes the smallest record there is.
	 */
	public boolean removeSmallestRecord() {
		REGTYPE r = getSmallestRecord();
		if (r!=null) {
			return remove(r.getID());
		}
		return false;
	}
	
	/**
	 * Removes the least frequently updated record.
	 */
	public boolean removeLeastFrequentlyUpdatedRecord() {
		REGTYPE r = getLeastFrequentlyUpdatedRecord();
		if (r!=null) {
			return remove(r.getID());
		}
		return false;
	}
	
	/**
	 * Gets a multiple data points object that you can use to perform
	 * statistical analysis with.  This will manage to make clones of the 
	 * records inside it, adjust them for lag with the interest records,
	 * adjust them for time frame differences, and adjust them for 
	 * frequency differences.
	 * @param records
	 * @return
	 */
	public MultipleDataPoints getMultipleDataPoints(RecordList<? extends Record> records) {
		if (records==null) {
//			logger.warn("Given null records.");
			throw new NullPointerException("Null records.");
		}
		if (records.size()<1) {
//			logger.warn("Given empty records.");
			throw new IllegalArgumentException("records are empty.");
		}
		if (items==null) {
//			logger.warn("Asked to get multiple data points while items is null.");
			throw new NullPointerException("Null items.");
		}
		if (items.size()<1) {
//			logger.warn("Asked to get multiple data points while items is empty.");
			throw new NullPointerException("empty items.");
		}
		if (getIDSet().size()!=this.size()) {
//			logger.warn("records provided has a duplicate.");
			throw new RuntimeException("Tried creating multiple datapoints, but this has a duplicate record!");
		}
//		logger.trace("Creating multiple data points for this set of records.");
		LiteDate ed = findEarliestEndDate(records);
		LiteDate minDate = LiteDate.getOrCreate(Calendar.MONTH,-2);
		if (ed.before(minDate)) {
			String s = "";
			for (RecordList<?> item : this.items) {
				if (item!=null)
					s+=item.getID()+": "+item.getStartDate()+" - "+item.getEndDate()+"\n";
			}
//			logger.warn("Building multiple data points from records, but the " +
//					"earliest possible end date is over two months in the past.\n\t"+
//					"min end date: "+minDate+", actual end date: "+ed+"\n"+s+"\n\t"+
//					"records time frame: "+records.getStartDate()+" - "+records.getEndDate());
		}
		Record r = records.getFirstRecordOnOrAfter(ed);
		ArrayList<MyRow> points = new ArrayList<MyRow>();
		// compute the lags
		int[] dayLags = new int[items.size()];
		int j = 0;
		int lag;
		for (RecordList<?> rs : items) {
			lag = rs.getLagInDays(r.getDate());
			if (lag<0) {
//				logger.warn("Found a negative lag!");
				lag=0;
			}
			dayLags[j] = lag; 
			j++;
		}
		boolean cont = true;
		String[] ids = new String[size()];
		int i = 0;
		for (RecordList<?> rs : items) {
			ids[i]=rs.getID();
			i++;
		}
		if (r==null) {
//			logger.warn("Before building the points, r is null.");
		}
//		logger.debug("In getMultipleDataPoints, going to build points now.");
		Double dr;
		LiteDate date;
		MyRow row;
		Double rr;
		while (r!=null) {
			i=0;
			dr = r.getValue();
			date = r.getDate();
			row = new MyRow();
			row.y = dr.doubleValue();
			row.x = new double[size()];
			for (RecordList<?> rs : items) {
				rr = rs.getMostRecentValueOnDate(date,dayLags[i]);
				if (rr==null) {
					cont=false;
					break;
				}
				row.x[i]=rr.doubleValue();
				i++;
			}
			if (!cont) {break;}
			points.add(row);
			r = records.getPrecedingRecord(r,5);
		}
//		logger.debug("In getMultipleDataPoints, going to build x and y points now.");
		// now must convert into multiple data points.
		double[] y = new double[points.size()];
		double[][] x = new double[points.size()][items.size()];
		for (int k=0;k<points.size();k++) {
			row = points.get(points.size()-k-1);
			y[k] = row.y;
			for (int m = 0;m<items.size();m++) {
				x[k][m] = row.x[m];
			}
		}
//		logger.debug("In getMultipleDataPoints, going to build the real object now.");
		MultipleDataPoints mdp = MultipleDataPoints.create(x, y,
				ids,records.getID(),getDerivativeStatus());
//		logger.trace("Finished creating multiple data points for this set of records.");
		return mdp;
	}
	
	private class MyRow {
		public double y = 0;
		public double[] x = null;
	}
	
	/**
	 * Finds the latest start date between all of the records inside this,
	 * and the interest provided.
	 * @param records
	 * @return
	 */
	public LiteDate findLatestStartDate(RecordList<? extends Record> records) {
		LiteDate sd = records.getStartDate();
		for (RecordList<? extends Record> r : this.items) {
			if (r.getStartDate().after(sd)) {
				sd = r.getStartDate();
			}
		}
		return sd;
	}
	
	/**
	 * Finds the earliest end date between all of the records inside this,
	 * and the interest provided.
	 * @param records
	 * @return
	 */
	public LiteDate findEarliestEndDate(RecordList<? extends Record> records) {
		LiteDate ed = records.getEndDate();
//		RecordList<? extends Record> worstRecord = null;
		for (RecordList<? extends Record> r : this.items) {
			if (r.getEndDate().before(ed)) {
				ed = r.getEndDate();
//				worstRecord=r;
			}
		}
		return ed;
	}
	
	/**
	 * Removes the records that are useless from runtime memory, and
	 * sorts the records to be in order from most useful to least 
	 * useful.  This way we are most likely to find a good prediction
	 * faster.
	 */
	public void prepareForRegressions() {
		removeUselessRecords();//true);
		sortByUsefulness();
	}
	
	/**
	 * Sorts the records to be in order of how useful they are based
	 * on a scoring method.
	 */
	public void sortByUsefulness() {
		LiteDate d1 = LiteDate.getOrCreate();
		d1.addYears(-8);
		LiteDate d2 = LiteDate.getOrCreate();
		d2.addMonths(-2);
		TimeFrame timeFrame = new TimeFrame(d1,d2);
		Comparator<REGTYPE> c = new UsefulnessComparator(timeFrame);
		ArrayList<REGTYPE> myList = new ArrayList<REGTYPE>(items);
		Collections.sort(myList,c);
		clear();
		addAll(myList);
	}
	
	private class UsefulnessComparator implements Comparator<REGTYPE> {
		private TimeFrame timeFrame = null;
		
		public UsefulnessComparator(TimeFrame timeFrame) {
			this.timeFrame = timeFrame;
		}

		@Override
		public int compare(REGTYPE firstRecords, REGTYPE secondRecords) {
			int firstScore = score(firstRecords);
			int secondScore = score(secondRecords);
			return secondScore-firstScore;
		}
		// higher score is better.
		public int score(REGTYPE records) {
			int size = records.validNonZeroEntriesInTimeFrame(timeFrame);
			int tu = records.getTimesUsedInFinalRegression();
			int score = size+2*tu;
			return score;
		}
	}
	
	public void removeUselessRecords(
//			boolean fromManagerToo
			) {
		System.out.println("Removing economic records that are useless, initially there are " +
				+size()+" records.");
		HashSet<String> removeThese = new HashSet<String>();
		LiteDate maxStartDate = LiteDate.getOrCreate(Calendar.YEAR, -8);
		LiteDate minEndDate = LiteDate.getOrCreate(Calendar.MONTH, -2);
		int minUsefulSize = 240;
		for (RecordList<?> r : this.items) {
			if (r.getUpdateFrequency().isLessFrequent(UpdateFrequency.MONTHLY)) {
				removeThese.add(r.getID());
			}
			if (r.isMostlyZeros()) {
				removeThese.add(r.getID());
			}
			if (r.getStartDate().after(maxStartDate) ||
					r.getEndDate().before(minEndDate) ||
					r.size()<minUsefulSize) {
				removeThese.add(r.getID());
			}
		}
		for (String s : removeThese) {
			remove(s);
//			if (fromManagerToo) {
//				if (this instanceof EconomicRecords) {
//					EconomicRecordManager.remove(s);
//				} else if (this instanceof PriceHistories) {
//					PriceHistoryManager.remove(s);
//				}
//			}
		}
		System.out.println("Removed "+removeThese.size()+" useless economic records," +
				" there are now "+size()+" records.");
	}

	public void incrementTimesUsedInRegressionResult() {
		for (REGTYPE r : this.items) {
			r.incrementTimesUsedInFinalRegression();
		}
	}
	
	public RecordSet<REGTYPE> getSubSet(int startIndex,int endIndex) {
		RecordSet<REGTYPE> ss = createInstance();
		ArrayList<REGTYPE> arl = new ArrayList<REGTYPE>(items);
		ss.addAll(arl.subList(startIndex, endIndex));
		return ss;
	}
	
	public boolean[] getDerivativeStatus() {
		boolean[] b = new boolean[this.items.size()];
		for (int i = 0;i<b.length;i++) {
			b[i]=items.get(i).isDerivative();
		}
		return b;
	}
	
	public final synchronized REGTYPE get(String id,boolean derivative) {
		Iterator<REGTYPE> iter = this.items.iterator();
		while (iter.hasNext()) {
			REGTYPE item = iter.next();
			if (item.getID().equals(id) && item.isDerivative()==derivative) {
				return item;
			}
		}
		return null;
	}
	
	public final synchronized boolean remove(String id,boolean derivative) {
		REGTYPE r = get(id,derivative);
		return items.remove(r);
	}

	public abstract RecordSet<REGTYPE> createInstance();
}