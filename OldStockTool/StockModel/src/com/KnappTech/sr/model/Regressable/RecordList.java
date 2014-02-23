package com.KnappTech.sr.model.Regressable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import com.KnappTech.model.KTObject;
import com.KnappTech.model.LiteDate;
import com.KnappTech.model.Regressable;
import com.KnappTech.model.TimeFrame;
import com.KnappTech.model.UpdateFrequency;
import com.KnappTech.sr.model.beans.ERBean;
import com.KnappTech.sr.model.beans.RecordListBean;
import com.KnappTech.sr.model.constants.SourceAgency;
import com.KnappTech.util.CodeStalker;
import com.KnappTech.util.DataPoints;
import com.KnappTech.util.Lock;
import com.KnappTech.util.MethodResponse;
import com.KnappTech.util.PredictableIndexArray;

public abstract class RecordList<E extends Record>
implements KTObject, Regressable//, 
//Reportable, ReportableSet 
{
	private static final long serialVersionUID = 201010021242L;
//	private static final Logger logger = Logger.getLogger(RecordList.class);
	
	protected final String id;
    protected final SourceAgency sourceAgency;
    
	protected PredictableIndexArray<E> records = new RecordListPIA<E>();
    protected LiteDate lastSuccessfulUpdate = null;
	protected LiteDate lastAttemptedUpdate = null;
	protected LiteDate startDate = null;
	protected LiteDate endDate = null;
	protected UpdateFrequency updateFrequency = null;
	protected boolean adjusted = false; // used to record whether or not the indicator
	// was adjusted for some other interest regressable list.
	private transient int timesUsedInFinalRegressionResults = 0;
	private transient final Lock lock = new Lock();
	private transient boolean derivative = false;
	private transient RecordList<E> derivativeMade = null;
	
	protected RecordList(String id, SourceAgency sourceAgency) {
		this.id = id;
		this.sourceAgency = sourceAgency;
	}
	
	protected RecordList(String id, SourceAgency sourceAgency,Collection<E> c) {
		this.id = id;
		this.sourceAgency = sourceAgency;
		records.addAll(c);
	}
	
	protected RecordList(RecordList<E> original) {
		this.id = original.id;
		this.sourceAgency = original.sourceAgency;
		this.lastSuccessfulUpdate = original.lastSuccessfulUpdate;
		this.lastAttemptedUpdate = original.lastAttemptedUpdate;
		this.startDate = original.startDate;
		this.endDate = original.endDate;
		this.updateFrequency = original.updateFrequency;
		this.adjusted = original.adjusted;
		this.records.addAll(original.records);
	}
	
	protected static void update(RecordList<?> r, RecordListBean bean) {
		r.lastSuccessfulUpdate = bean.getLastSuccessfulUpdate();
		r.lastAttemptedUpdate = bean.getLastAttemptedUpdate();
//		r.startDate = bean.getStartDate();
//		r.endDate = bean.getEndDate();
//		r.updateFrequency = bean.getUpdateFrequency();
//		r.adjusted = bean.isAdjusted();
		for (Record rr : bean.getRecords()) {
			r.addOrReplace(rr.getDate(),rr.getValue());
		}
	}
	
	public String getID() {
		return id;
	}
	
	public SourceAgency getSourceAgency() {
		return sourceAgency;
	}
	
	public void setLastSuccessfulUpdate(LiteDate lastSuccessfulUpdate) {
		this.lastSuccessfulUpdate = lastSuccessfulUpdate;
	}
	public LiteDate getLastSuccessfulUpdate() {
		return lastSuccessfulUpdate;
	}
	public void setLastAttemptedUpdate(LiteDate lastAttemptedUpdate) {
		this.lastAttemptedUpdate = lastAttemptedUpdate;
	}
	public LiteDate getLastAttemptedUpdate() {
		return lastAttemptedUpdate;
	}
	public void setAdjusted(boolean adjusted) {
		this.adjusted = adjusted;
	}
	public boolean isAdjusted() {
		return adjusted;
	}
	
	/**
	 * Decides if two record lists are equal, based on their id only.
	 */
	public boolean equals(Object o) {
		if (o instanceof RecordList<?>) {
			RecordList<?> r = (RecordList<?>)o;
			String rid = r.getID();
			if (rid!=null) {
				return rid.equals(id);
			}
			return (rid==null && id==null);
		}
		return false;
	}
	
	
	public int hashCode() {
		if (id!=null) {
			return id.hashCode()+5;
		}
		return -9;
	}
	
	
	public String toString() {
		String s = id+", "+sourceAgency.name()+"\n";
		if (records.size()<5) {
			for (Record r : records) {
				s+=r.toString();
			}
		} else {
			for (int i = 0;i<2;i++) {
				s+=records.getItem(i).toString();
			}
			s+="~~~~ abridged ~~~~\n";
			for (int i = records.size()-2;i<records.size();i++) {
				s+=records.getItem(i).toString();
			}
		}
		return s;
	}

	/**
	 * Finds the first record that has a date.
	 * @return
	 */
    public E getFirstRecord() {
    	if (records.size()>0) {
    		return records.iterator().next();
//	    	Iterator<E> iter = records.iterator();
//	    	E r = null;
//	    	LiteDate d = null;
//	    	while (iter.hasNext()) {
//	    		r = iter.next();
//	    		d = r.getDate();
//	    		if (d!=null)
//	    			return r;
//	    	}
    	}
    	return null;
    }

    /**
     * Counts from the end all records that have a date, 
     * @param num
     * @return the num record from the end that has a date.
     */
    public E getRecordFromEnd(int num) {
    	if (num<0 || num>=records.size())
    		throw new IndexOutOfBoundsException("Requested index "+num+" from end, " +
    				"but size is "+records.size());
		E r = records.getItem(records.size()-num-1);
//		if (r==null) {
//	    	logger.warn("Size is "+records.size()+", capacity "+records.capacity()+
//	    			".  Want num from end "+num+", requesting true index "+(records.size()-num-1));
//	    	logger.warn("Records is indexed: "+String.valueOf(records.wasIndexed()));
//	    	logger.warn("This is locked: "+String.valueOf(isLocked()));
//		}
		return r;
//    		int count = 0;
//    		E r = null;
//    		Iterator<E> iter = records.iterator(false);
//    		while (iter.hasNext()) {
//    			r = iter.next();
//	    		if (count>=num)
//	    			return r;
//	    		count++;
//    		}
//	    	for (int i = records.size()-1;i>=0;i--) {
//		    	r = records.get(i);
//		    	if (r!=null && r.getDate()!=null) {
//		    		if (count>=num)
//		    			return r;
//		    		count++;
//		    	}
//	    	}
//    	return null;
    }

    /**
     * Finds the last record that has a date.
     * @return
     */
    public E getLastRecord() {
    	return getRecordFromEnd(0);
    }
    
    /**
     * Gets the internal copy of the start date.
     * @return
     */
    public LiteDate getStartDate() {
    	if (startDate==null && records.size()>0) {
    		return determineStartDate();
    	}
    	return startDate;
    }

    /**
     * Calculates the start date.  This should not be necessary,
     * the add methods should keep the internal variable accurate.
     * @return
     */
    public LiteDate determineStartDate() {
    	E r = getFirstRecord();
    	if (r!=null) {
    		startDate = r.getDate();
    		return r.getDate();
    	}
    	return null;
    }
    
    /**
     * Tells you the internal variable for the stored end date.
     * @return
     */
    public LiteDate getEndDate() {
    	if (endDate==null && records.size()>0) {
    		return determineEndDate();
    	}
    	return endDate;
    }
    
    /**
     * Determines the end date and stores it as the internal
     * variable.
     * @return
     */
    public LiteDate determineEndDate(){
    	E r = getLastRecord();
    	if (r!=null) {
    		endDate = r.getDate();
    		return r.getDate();
    	}
    	return null;
    }
    
    /**
     * Gets update frequency from local variable.  Make sure that you
     * have calculated it with determineUpdateFrequency first.
     * @return
     */
    public UpdateFrequency getUpdateFrequency() {
    	return updateFrequency;
    }
    
    /**
     * Determines the update frequency using the last two 
     * records known.  Saves the calculation as the internal
     * state variable updateFrequency, so later calls don't
     * require recalculating it.
     * @return
     */
    public UpdateFrequency determineUpdateFrequency() {
//    	logger.info("Size is "+records.size()+", capacity "+records.capacity()+
//    			".  Want num from end 1, requesting true index "+(records.size()-1-1));
//    	logger.info("Records is indexed: "+String.valueOf(records.wasIndexed()));
//    	logger.info("This is locked: "+String.valueOf(isLocked()));
    	E r1 = getRecordFromEnd(1);
     	E r2 = getLastRecord();
//     	if (r1==null) {
//     		logger.warn("get record from end (1) returned null.");
//	    	logger.warn("Size is "+records.size()+", capacity "+records.capacity()+
//	    			".  Want num from end 1, requesting true index "+(records.size()-1-1));
//	    	logger.warn("Records is indexed: "+String.valueOf(records.wasIndexed()));
//	    	logger.warn("This is locked: "+String.valueOf(isLocked()));
//     	}
//     	if (r2==null) {
//     		logger.warn("get last record returned null.");
//	    	logger.warn("Size is "+records.size()+", capacity "+records.capacity()+
//	    			".  Want num from end 1, requesting true index "+(records.size()-1-1));
//	    	logger.warn("Records is indexed: "+String.valueOf(records.wasIndexed()));
//	    	logger.warn("This is locked: "+String.valueOf(isLocked()));
//     	}
    	LiteDate d1 = r1.getDate();
    	LiteDate d2 = r2.getDate();
    	LiteDate pd = d2.getPriorWeekDay();
    	
    	if (d1.equals(pd)) {
    		updateFrequency = UpdateFrequency.DAILY;
    	} else {
    		updateFrequency = UpdateFrequency.estimate(d1, d2);
    	}
    	return updateFrequency;
    }
    
    /**
     * Finds the most recent value on the date provided.
     * @param d
     * @return
     */
    public E getMostRecentOn(LiteDate d) {
    	return getMostRecentOn(d,0);
    }
    
    /**
     * Gets the most recent record on the date specified.  You can 
     * apply a lag with minDaysBefore.  For example, if there is a 
     * value on the first day of every month, it is currently the 
     * third day of the month, and you ask for the most recent value
     * today with a five day lag, it will return the entry on the
     * first day of last month, not the first day of this month.
     * <p>Will include the value that falls exactly on the date.</p>
     * @param d
     * @return
     */
    public E getMostRecentOn(LiteDate dd,int minDaysBefore) {
//    	logger.trace("Trying to find most recent on date.");
    	try {
	    	if (dd!=null && records.size()>0) {
	    		LiteDate d = dd.addDays(-minDaysBefore);
	    		if (d.before(startDate))
	    			return null;
	    		// try fast first:
	    		return records.getItemOnOrBefore(dd);
//	    		int ind = getIndexOfMostRecentOn(d);
//	    		if (ind>=0 && ind<records.size()) {
//		    		E record = records.get(ind);
//		    		// the most recent record can include the present date:
//		    		// is on or before = !after
//		    		if (record.getDate().onOrBefore(d)) {
//		    			return record;
//		    		} else if (record.getDate().after(d)) {
//		    			if (ind>0 && records.get(ind-1).getDate().before(d)) {
//	    					return records.get(ind-1);
//	    				} else if (ind>0) {
//			        		logger.warn("Tried to find most recent record by fast method. Asked for "+dd+
//			        				", with mindays before: "+minDaysBefore+", but got a record after that date.");
//	    				} else if (ind==0) {
//	    					return null;
//	    				}
//		    		}
//	    		}
//	    		logger.warn("failed to find most recent record by fast method. Asked for "+dd+
//	    				", with mindays before: "+minDaysBefore+", start date: "+startDate+
//	    				", index: "+ind+", now doing the slow method.");
//	    		// try sure method second:
//	    		E mostRecent = getFirstValidRecord();
//	    		LiteDate rd = null;
//		    	for (E r : records) {
//		    		rd = r.getDate();
//		    		if (rd!=null) {
//			    		if (r.getDate().equals(d)) {
//			    			return r;
//			    		} else if (rd.after(mostRecent.getDate()) && rd.before(d)) {
//			    			mostRecent=r;
//			    		} else if (rd.after(d))
//			    		{	// I am assuming this is in order from oldest
//			    			// to newest, which it always should be.
//			    			break;
//			    		}
//		    		}
//		    	}
//		    	return mostRecent;
	    	}
	    	return null;
    	} finally {
//    		logger.trace("Finished trying to find most recent on "+dd);
    	}
    }
    
    /**
     * Gets the record at the index specified.
     * @param index
     * @return
     */
    public E getRecord(int index) {
    	if (index>=0 && index<records.capacity()) {
    		return records.getFromInternalIndex(index);
    	}
    	return null;
    }
    
    /**
     * Gets the record with the exact date specified.
     * @param d
     * @return the record with the exact date specified.
     */
    public E getRecord(LiteDate d) {
    	if (d!=null && records.size()>0) {
    		// try fast first:
    		return records.get(d);
//    		int ind = getIndexOfMostRecentOn(d);
//	    	if (ind>=0 && ind<records.size()) {
//	    		E record = records.get(ind);
//	    		if (record.getDate().equals(d))
//	    			return record;
//    		}
//    		// try the sure method second.
//	    	LiteDate rd = null;
//	    	for (E r : records) {
//	    		rd = r.getDate();
//	    		if (rd!=null && rd.equals(d))
//	    			return r;
//	    	}
    	}
    	return null;
    }
    
    /**
     * Gets the record value on the date specified if and only
     * if there is an entry on that exact date.
     * @param d
     * @return
     */
    public Double getValueOnExactDate(LiteDate d) {
    	E r = getRecord(d);
    	if (r!=null) {
    		return r.getValue();
    	}
    	return null;
    }
    
    /**
     * Gets the value at the index provided in the list,
     * which says nothing about the time it happened.
     * @param i
     * @return
     */
    public Double getValueAtIndex(int i) {
    	if (i>=0 && i<size()) {
    		E r = records.getItem(i);
    		if (r!=null) {
    			return r.getValue();
    		}
    	}
    	return null;
    }
    
    /**
     * Finds the last known value on the date specified.
     * @param d
     * @return
     */
    public Double getMostRecentValueOnDate(LiteDate d) {
    	return getMostRecentValueOnDate(d, 0);
    }
    
    /**
     * Finds the last known value on the date specified.  You can apply 
     * a lag here using minDaysBefore.
     * @param d
     * @return
     */
    public Double getMostRecentValueOnDate(LiteDate d,int minDaysBefore) {
    	E r = getMostRecentOn(d,minDaysBefore);
    	if (r!=null) {
    		return r.getValue();
    	}
    	return null;
    }
	
    /**
     * Determines if there is a non-null value at the index
     * provided.
     * @param index
     * @return
     */
	public boolean isThereAValidValueAt(int index) {
		if (index>=0 && index<size() && records!=null && 
				records.size()>0){
			E r = records.getItem(index);
			return (r!=null && r.getValue()!=null);
		}
		return false;
	}
    
	/**
	 * Finds the date of the first entry whose date and value are not null.
	 * @return
	 */
    public LiteDate getFirstDate() {
    	return records.getFirst().getDate();
    }
    
    /**
     * Finds the date of the last entry whose date and value are not null.
     * @return
     */
    public LiteDate getLastDate() {
    	return records.getLast().getDate();
    }
    
    /**
     * Gets the value of the first entry that has a non-null date and value.
     * @return
     */
    public Double getFirstValue() {
    	return records.getFirst().getValue();
    }
    
    /**
     * Gets the value of the last entry that has a non-null date and value.
     * @return
     */
    public Double getLastValue() {
    	return records.getLast().getValue();
    }
    
    public Double getLastDerivativeValue() {
    	if (isDerivative())
    		return getLastValue();
    	Double d1 = records.getItem(size()-2).getValue();
    	Double d2 = records.getLast().getValue();
    	Double v = d2-d1;
    	return v;
    }
    
    /**
     * Finds the maximum absolute value.
     * @return
     */
    public Double getMaxAbsoluteValue() {
    	E r = getAbsoluteMaxRecord();
    	if (r==null)
    		return null;
    	return r.getValue();
    }
    
    public Double getMinAbsoluteValue() {
    	E r = getAbsoluteMinRecord();
    	if (r==null)
    		return null;
    	return r.getValue();
    }
    
    public Double getMaxValue() {
    	E r = getMaxRecord();
    	if (r==null)
    		return null;
    	return r.getValue();
    }
    
    public Double getMinValue() {
    	E r = getMinRecord();
    	if (r==null)
    		return null;
    	return r.getValue();
    }
    
    public E getAbsoluteMaxRecord() {
    	return getAbsoluteMinMaxRecord(true);
    }
    
    public E getAbsoluteMinRecord() {
    	return getAbsoluteMinMaxRecord(false);
    }
    
    public E getMaxRecord() {
    	return getMinMaxRecord(true);
    }
    
    public E getMinRecord() {
    	return getMinMaxRecord(false);
    }
    
    public E getMinMaxRecord(boolean max) {
    	int i = getIndexOfMinMax(max);
    	if (i>=0 && i<size()) {
    		return records.getItem(i);
    	}
    	return null;
    }
    
    public E getAbsoluteMinMaxRecord(boolean max) {
    	int i = getIndexOfAbsoluteMinMax(max);
    	if (i>=0 && i<size()) {
    		return records.getItem(i);
    	}
    	return null;
    }
    
    public int getIndexOfAbsoluteMinMax(boolean max) {
    	return getIndexOfAbsoluteMinMax(max, true);
    }
    
    public int getIndexOfAbsoluteMinMax(boolean max,boolean justChecking) {
		if (records!=null && records.size()>0) {
			Double extrema = null;
			int extremaIndex = -1;
			E r;
			Double v;
			for (int i = 0;i<size();i++) {
				r = records.getItem(i);
				if (r!=null && r.getValue()!=null) {
					v = Math.abs(r.getValue());
					if (extrema==null || 
						(max && v>extrema) || 
						(!max && v<extrema)) {
						extrema = v;
						extremaIndex=i;
					}
				}
			}
			return extremaIndex;
		} else if (!justChecking) {
    		System.err.println("Asked to find date of an index while records are null/empty.");
    	}
		return -1;
    }
    
    public int getIndexOfMinMax(boolean max) {
    	return getIndexOfMinMax(max, false);
    }
    
    public int getIndexOfMinMax(boolean max,boolean justChecking) {
		if (records!=null && records.size()>0) {
			Double extrema = null;
			int extremaIndex = -1;
			E r;
			Double v;
			for (int i = 0;i<size();i++) {
				r = records.getItem(i);
				if (r!=null && r.getValue()!=null) {
					v = r.getValue();
					if (extrema==null || 
						(max && v>extrema) || 
						(!max && v<extrema)) {
						extrema = v;
						extremaIndex=i;
					}
				}
			}
			return extremaIndex;
		} else if (!justChecking)  {
    		System.err.println("Asked to find date of an index while records are null/empty.");
    	}
		return -1;
    }
    
    /**
     * Finds the date of the record at index, starting from zero.
     * @param index
     * @return
     */
    public LiteDate getDate(int index) {
		if (records!=null && records.size()>0) {
			if (index>=0 && index<size()) {
				E r = records.getItem(index);
				if (r!=null) {
					return r.getDate();
				} else {
		    		System.err.println("The record at index "+index+" is null.");
		    	}
	    	} else {
	    		System.err.println("Given invalid index to find date of. "+index);
	    	}
		} else {
    		System.err.println("Asked to find date of an index while records are null/empty.");
    	}
		return null;
    }
    
    /**
     * Finds the date of the record from the end.  Use zero to 
     * find the date of the last record.
     * @param num
     * @return
     */
    public LiteDate getDateFromEnd(int num) {
		if (records!=null && records.size()>0) {
			if (num>=0 && num<size()) {
				E r = records.getItem(size()-1-num);
				if (r!=null) {
					LiteDate d = r.getDate();
					if (d==null) {
			    		System.err.println("The record at num "+num+" has a null date.");
					}
					return d;
				} else {
		    		System.err.println("The record at num "+num+" is null.");
		    	}
	    	} else {
	    		System.err.println("Given invalid num to find date of. "+num);
	    	}
		} else {
    		System.err.println("Asked to find date of an num while records are null/empty.");
    	}
		return null;
    }
    
    /**
     * Determines how many entries are present with null values.
     * @return
     */
    
    public int getCountOfNullValues() {
    	if (records!=null && records.size()>0) {
    		int sum = 0;
    		for (E r : records)
    			if (r.getValue()==null)
    				sum++;
    		return sum;
    	}
    	return 0;
    }
    

    public int getCountOfZeroValues() {
    	if (records==null || records.size()<1)
    		return 0;
		int sum = 0;
		for (E r : records)
			if (r.getValue()==null || r.getValue()==0)
				sum++;
		return sum;
    }

    public int getCountOfChanges() {
    	if (records==null || records.size()<1)
    		return 0;
		int sum = 0;
		double lastValue = records.getItem(0).getValue();
		for (E r : records) {
			if (r.getValue()!=lastValue)
				sum++;
			lastValue = r.getValue();
		}
		return sum;
    }
    
    /**
     * Gets an array of doubles with the values of this record list
     * in chronological order.
     * @return
     */
    public double[] getPoints(LiteDate minDate) {
    	ArrayList<Double> values = new ArrayList<Double>();
    	if (records==null)
    		return null;
		for (E r : records) {
			if (minDate==null || (r.getDate().onOrAfter(minDate)) && 
					r.getValue()!=null)
				values.add(r.getValue());
		}
    	double[] x = new double[values.size()];
    	for (int i = 0;i<values.size();i++) {
    		x[i]=(double)values.get(i);
    	}
    	return x;
    }
    
    /**
     * Converts this to a datapoints object, making it easy to do all
     * kinds of statistical work with it.
     * @return
     */
    public DataPoints getDataPoints(LiteDate minDate) {
    	double[] x = getPoints(minDate);
    	DataPoints dp = new DataPoints(x,x);
    	return dp;
    }
    
    /**
     * Converts this and the other indicator to a data points object, 
     * making it real easy to do statistical analysis on them.
     * @param indicator
     * @return
     */
    public DataPoints getDataPoints(RecordList<?> indicator,LiteDate minDate) {
    	ArrayList<LiteDate> dates = getCommonDates(indicator);
    	double[] x = getPoints(dates);
    	double[] y = indicator.getPoints(dates);
    	DataPoints dp = new DataPoints(x,y);
    	return dp;
    }
    
    /**
     * Gets a datapoints object by using the percent returns
     * made on each day.
     * @return
     */
    public DataPoints getReturnsDataPoints(LiteDate minDate) {
    	return getReturnsDataPoints(this, minDate);
//    	double[] xrs = getReturns(getAvailablePoints(minDate));
//    	DataPoints dp = new DataPoints(xrs,xrs);
//    	return dp;
    }
    
    /**
     * Gets a datapoints object by using the percent returns this and
     * the indicator each made on each day.
     * @param indicator
     * @return
     */
    public DataPoints getReturnsDataPoints(RecordList<E> indicator,LiteDate minDate) {
    	ArrayList<LiteDate> dates = getCommonDates(indicator);
    	double[] xrs = getReturns(getPoints(dates));
    	double[] yrs = getReturns(indicator.getPoints(dates));
    	DataPoints dp = new DataPoints(xrs,yrs);
    	return dp;
    }
    
    /**
     * Calculates the standard deviation of the values.
     * @return
     */
    public double calculateStandardDeviation(LiteDate minDate) {
    	DataPoints dp = getDataPoints(minDate);
    	if (dp!=null)
    		return dp.getStandardDeviationX(minDate==null);
    	return 0;
    }
    
//    /**
//     * Gets a list of all the lite dates available.
//     * @return
//     */
//    public ArrayList<LiteDate> getAvailableDates() {
//    	// use a set to avoid duplicates.
//    	LinkedHashSet<LiteDate> lhs = new LinkedHashSet<LiteDate>();
//    	for (E r : records) {
//    		LiteDate d = r.getDate();
//    		if (d!=null && r.getValue()!=null) {
//    			lhs.add(d);
//    		}
//    	}
//    	ArrayList<LiteDate> arl = new ArrayList<LiteDate>(lhs);
//    	return arl;
//    }
    
//    /**
//     * Gets a list of all of the values available.
//     * @return
//     */
//    public ArrayList<Double> getAvailableValues(LiteDate minDate) {
//    	// use a set to avoid duplicates.
//    	ArrayList<Double> arl = new ArrayList<Double>();
//    	for (E r : records) {
//    		if (r.getDate()!=null && r.getValue()!=null && 
//    			(minDate==null || r.getDate().after(minDate)) ) 
//    		{
//    			arl.add(r.getValue());
//    		}
//    	}
//    	return arl;
//    }
//    
//    public double[] getAvailablePoints(LiteDate minDate) {
//    	ArrayList<Double> arl = getAvailableValues(minDate);
//    	double[] ds = new double[arl.size()];
//    	for (int i = 0;i<arl.size();i++) {
//    		ds[i] = arl.get(i).doubleValue();
//    	}
//    	return ds;
//    }
    
    /**
     * Finds all of the dates that are covered in both lists, the dates
     * must match exactly between the two lists for them to be included
     * in the returned list.  Each date must have a non-null value for
     * both this and the other list to be included.
     * @param otherList
     * @return
     */
    public ArrayList<LiteDate> getCommonDates(RecordList<?> otherList) {
    	// use a set to avoid duplicates.
    	LinkedHashSet<LiteDate> lhs = new LinkedHashSet<LiteDate>();
    	E mr;
    	LiteDate od;
    	for (Record or : otherList.records) {
    		od = or.getDate();
    		if (od==null || or.getValue()==null)
    			continue;
			mr = getRecord(od);
			if (mr!=null && mr.getValue()!=null)
				lhs.add(od);
    	}
    	ArrayList<LiteDate> arl = new ArrayList<LiteDate>(lhs);
    	return arl;
    }
    
    /**
     * Gets a list of records this has whose exact date is present
     * in the collection of dates provided.  The exact date must 
     * match, if it is missing then no prior value may be used, 
     * and the size will not match the size of the dates you provided.
     * @param dates
     * @return
     */
    public ArrayList<E> getRecords(Collection<LiteDate> dates) {
    	ArrayList<E> rs = new ArrayList<E>();
    	E r;
    	for (LiteDate td : dates) {
    		r = getRecord(td);
    		if (r!=null) {
    			rs.add(r);
    		} else {
    			System.err.println("Asked for a record on a date that was not available.");
    		}
    	}
    	return rs;
    }
    
    /**
     * Gets a list of values on the exact dates provided.  The dates
     * must match exactly.
     * @param dates
     * @return
     */
    public ArrayList<Double> getValues(Collection<LiteDate> dates) {
    	ArrayList<E> rs = getRecords(dates);
    	ArrayList<Double> vs = new ArrayList<Double>();
    	for (E r : rs) {
    		vs.add(r.getValue());
    	}
    	return vs;
    }
    
    /**
     * Gets an array of values on the exact dates specified.
     * @param dates
     * @return
     */
    public double[] getPoints(Collection<LiteDate> dates) {
    	ArrayList<Double> rs = getValues(dates);
    	double[] ds = new double[rs.size()];
    	for (int i = 0;i<rs.size();i++) {
    		ds[i] = rs.get(i).doubleValue();
    	}
    	return ds;
    }
    
    public ArrayList<Double> getValues() {
    	ArrayList<Double> vs = new ArrayList<Double>();
    	for (E r : records) {
    		vs.add(r.getValue());
    	}
    	return vs;
    }
    
    /**
     * Gets an array of the percent return values made between
     * two entries.
     * @param x
     * @return
     */
    public static double[] getReturns(double[] x) {
    	double[] returns = new double[x.length-1];
    	for (int i = 0;i<x.length-1;i++) {
    		if (x[i]!=0) {
    			returns[i] = (x[i+1]/x[i]-1);
    		} else {
    			returns[i]=0;
    			System.err.println("Found a zero value in provided points.");
    		}
    	}
    	return returns;
    }
    
    public double calculateCovarianceOfReturn(RecordList<E> indicator) {
    	return calculateCovarianceOfReturn(indicator, null);
    }
	
	public double calculateVarianceOfReturn() {
    	return calculateCovarianceOfReturn(null);
		
	}
    
    public double calculateCovarianceOfReturn(RecordList<E> indicator,LiteDate minDate) {
    	DataPoints dp = getReturnsDataPoints(indicator,minDate);
    	double cov = dp.getCovariance();
    	return cov;
    }
	
	public double calculateVarianceOfReturn(LiteDate minDate) {
		DataPoints dp = getReturnsDataPoints(minDate);
//		DataPoints dp2 = getReturnsDataPoints(this,minDate);
    	double cov = dp.getVarianceX(minDate==null);
    	return cov;
	}
	
	public double calculateVariance(LiteDate minDate) {
		DataPoints dp = getDataPoints(minDate);
    	double cov = dp.getVarianceX(minDate==null);
    	return cov;
	}
	
	public double calculateCovariance(RecordList<E> indicator,LiteDate minDate) {
		DataPoints dp = getDataPoints(indicator, minDate);
		double cov = dp.getCovariance();
		return cov;
	}
	
	public double calculateVolatility(LiteDate minDate) {
		DataPoints dp = getDataPoints(minDate);
    	double vol = dp.getVolatilityX();
    	return vol;
	}
	
	public double calculateAverage(LiteDate minDate) {
		DataPoints dp = getDataPoints(minDate);
    	double avg = dp.getAverageX();
    	return avg;
	}
    
    /**
     * Adds a record if and only if there is not already one present
     * with the same date.  So this will never replace an existing value.
     * @param date
     * @param value
     * @return
     */
    public boolean addNew(LiteDate date,double value) {
    	if (date==null) 
    		throw new NullPointerException();
    	if (this.lock.isLocked())
    		throw new IllegalStateException("Cannot modify once locked.");
    	E record = createRecord(date, value);
    	return addIfNew(record);
    }
    
    /**
     * Creates a record and then does add or replace
     * @param date
     * @param value
     * @return
     */
    public boolean addOrReplace(LiteDate date,double value) {
    	if (date==null)
    		throw new NullPointerException();
    	if (this.lock.isLocked())
    		throw new IllegalStateException("Cannot modify once locked.");
    	E record = createRecord(date, value);
    	return addOrReplace(record);
    }
    
    /**
     * Adds a record if it is new only
     * @param r
     * @return true if the record is new and was successfully added.
     */
    public boolean addIfNew(E r)
    {	// see if it can be added safely.
    	if (r==null) 
    		throw new NullPointerException();
    	if (this.lock.isLocked())
    		throw new IllegalStateException("Cannot modify once locked.");
		if (records.size()>0)
		{	// I will not allow any duplicate records:
    		if (!records.contains(r)) {
    			// we don't just want to add it anywhere, it 
    			// should be in order from most old to most recent:
				if (endDate==null || r.getDate().after(endDate))
					endDate = r.getDate();
				if (startDate==null || r.getDate().before(startDate))
					startDate = r.getDate();
				records.add(r);
				// audit the update frequency 
				if (updateFrequency==null || (size()>=100 && Math.log10(size())%1==0)){
					determineUpdateFrequency();
				}
				return true;
    		} // will not allow modification of an existing value here.
		} else { // records is empty right now.  So first record here:
			if (startDate==null || r.getDate().before(startDate))
				startDate = r.getDate();
			if (endDate==null || r.getDate().after(endDate))
				endDate = r.getDate();
			records.add(r);
			return true;
		}
    	return false;
    }
    
    /**
     * Adds the record or updates the value of an existing one.
     * @param r
     * @return true if successful.
     */
    public boolean addOrReplace(E r) {
    	if (r==null) 
    		throw new NullPointerException();
    	if (this.lock.isLocked())
    		throw new IllegalStateException("Cannot modify once locked.");
		if (records.contains(r))
			return records.replace((E)r);
		else
    		return addIfNew(r);
    }
    
    /**
     * An ultra fast method to find the index of the date,
     * this should help the application run fast.  It works
     * on the assumption that it is in order from oldest to 
     * newest, and uses some math and iteration to find the index.
     * @param dateInquiry
     * @return the index.
     */
    public int getIndexOfMostRecentOn(LiteDate dateInquiry) {
    	if (dateInquiry!=null && records.size()>0) {
    		if (startDate==null)
    			determineStartDate();
    		if (endDate==null)
    			determineEndDate();
    		if (startDate.after(dateInquiry))
    			return -1;
    		if (endDate.before(dateInquiry))
    			return records.size()-1;
    		if (startDate.equals(dateInquiry))
    			return 0;
    		if (endDate.equals(dateInquiry))
    			return records.size()-1;
    		int in = records.getIndexDeterminer().determineIndex(dateInquiry);
    		E r = records.getFromInternalIndex(in);
    		while (r==null && in>0) {
    			in--;
    			r=records.getFromInternalIndex(in);
    		}
    		return (r==null ? -1 : in);
//    		int startDateTime = (startDate.hashCode());
//    		int endDateTime = (endDate.hashCode());
//    		double recordElapsedTime = endDateTime-startDateTime;
//    		int dateInquiryTime = (dateInquiry.hashCode());
//    		double elapsedTime = dateInquiryTime-startDateTime;
//    		int index = (int)(Math.round((elapsedTime/recordElapsedTime)*size()));
//    		if (index<1) index = 1;
//    		if (index>=size()) index=size()-1;
//    		E r = records.get(index);
//    		LiteDate indDate = r.getDate();
//    		while (indDate.before(dateInquiry) && index<size()-1) {
//    			index++;
//    			r = records.get(index);
//    			indDate = r.getDate();
//    		}
//    		while ((indDate==null || r.getValue()==null || indDate.after(dateInquiry)) && index>0) {
//    			index--;
//    			r = records.get(index);
//    			indDate = r.getDate();
//    		}
//    		if (index<0) {
//    			logger.warn("Computed a negative index while the date inquiry is on or after the start date.");
//    		}
//    		return index;
    	}
    	return 0;
    }
    
    /**
     * Tells you the number of record entries it holds.
     * @return
     */
    public int size() {
    	if (records!=null) {
    		return records.size();
    	}
    	return 0;
    }
    
//    /**
//     * Finds the size of this by only counting entries with non-null dates.
//     * @return
//     */
//    public int sizeOfDates() {
//    	if (records!=null) {
//    		int sum = 0;
//    		for (Record r : this.records) {
//    			if (r.getDate()!=null) {
//    				sum++;
//    			}
//    		}
//    		return sum;
//    	}
//    	return 0;
//    }
    
//    /**
//     * Gets the size by counting only entries whose date and value are not null.
//     * @return
//     */
//    public int sizeOfValidEntries() {
//    	if (records!=null) {
//    		int sum = 0;
//    		for (Record r : this.records) {
//    			if (r.getDate()!=null && r.getValue()!=null) {
//    				sum++;
//    			}
//    		}
//    		return sum;
//    	}
//    	return 0;
//    }
    
//    /**
//     * Gets the size by counting only entries that have a value.
//     * @return
//     */
//    public int sizeOfValues() {
//    	if (records!=null) {
//    		int sum = 0;
//    		for (Record r : this.records) {
//    			if (r.getValue()!=null) {
//    				sum++;
//    			}
//    		}
//    		return sum;
//    	}
//    	return 0;
//    }
    
    /**
     * Removes all records whose values are null.
     */
    public void removeRecordsWithoutValues() {
    	if (records!=null) {
    		Iterator<E> iter = records.iterator();
    		while (iter.hasNext()) {
    			E r = iter.next();
    			if (r.getValue()==null) {
    				iter.remove();
    			}
    		}
    	}
    }
    
    /**
     * Removes all records whose date is null.
     */
    public void removeRecordsWithoutDates() {
    	if (records!=null) {
    		Iterator<E> iter = records.iterator();
    		while (iter.hasNext()) {
    			E r = iter.next();
    			if (r.getDate()==null) {
    				iter.remove();
    			}
    		}
    	}
    }
    
    /**
     * Removes records whose date or value is null.
     */
    public void removeInvalidRecords() {
    	if (records!=null) {
    		Iterator<E> iter = records.iterator();
    		while (iter.hasNext()) {
    			E r = iter.next();
    			if (r.getDate()==null || r.getValue()==null) {
    				iter.remove();
    			}
    		}
    	}
    }
    
//    /**
//     * Adds records to the end until the size is reached.
//     * @param size
//     * @return
//     */
//    public int appendNullsToReachSize(int size) {
//    	int numberAdded = 0;
//    	if (records!=null && records.size()>0) {
//	    	while (size()<size) {
//	    		LiteDate d = endDate.dateOffset(updateFrequency, 1, true);
//	    		E record = createRecord(d);
//	    		if (addIfNew(record)) {
//		    		numberAdded++;
//		    	}
//	    	}
//    	} else {
//    		System.err.println("Tried appending nulls when this has no records.");
//    	}
//    	adjusted = true;
//    	return numberAdded;
//    }
    
//    /**
//     * Adds records to the end until the size is reached.
//     * @param size
//     * @return
//     */
//    public int prependNullsToReachSize(int size) {
//    	int numberAdded = 0;
//    	if (records!=null && records.size()>0) {
//	    	while (size()<size) {
//	    		LiteDate d = startDate.dateOffset(updateFrequency, -1, true);
//	    		E record = createRecord(d);
//	    		if (addIfNew(record)) {
//		    		numberAdded++;
//		    	}
//	    	}
//    	} else {
//    		System.err.println("Tried appending nulls when this has no records.");
//    	}
//    	adjusted = true;
//    	return numberAdded;
//    }
    
//    /**
//     * Adds records to the end until the size is reached.
//     * @param size
//     * @return
//     */
//    public int appendNullsToDate(LiteDate d) {
//    	int numberAdded = 0;
//    	if (records!=null && records.size()>0) {
//	    	while (endDate.before(d)) {
//	    		LiteDate ed = endDate.dateOffset(updateFrequency, 1, true);
//	    		E record = createRecord(ed);
//	    		if (addIfNew(record)) {
//		    		numberAdded++;
//		    	}
//	    	}
//    	} else {
//    		System.err.println("Tried appending nulls when this has no records.");
//    	}
//    	adjusted = true;
//    	return numberAdded;
//    }
    
//    /**
//     * Adds records to the end until the size is reached.
//     * @param size
//     * @return
//     */
//    public int prependNullsToDate(LiteDate d) {
//    	int numberAdded = 0;
//    	if (records!=null && records.size()>0) {
//	    	while (startDate.after(d)) {
//	    		LiteDate sd = startDate.dateOffset(updateFrequency, -1, true);
//	    		E record = createRecord(sd);
//	    		if (addIfNew(record)) {
//		    		numberAdded++;
//		    	}
//	    	}
//    	} else {
//    		System.err.println("Tried appending nulls when this has no records.");
//    	}
//    	adjusted = true;
//    	return numberAdded;
//    }
//	
//    /**
//     * Adjusts the date of every entry for lag between this and the date provided.
//     * First it estimates the lag, then it shifts all the dates using the lag
//     * it found.
//     * @param interestLastDate
//     */
//	public void adjustForLag(LiteDate interestLastDate) {
//		if (interestLastDate!=null) {
//			if (!records.isEmpty()) {
//				int lag = estimateLag(interestLastDate);
//				if (lag>0 && lag<size()) {
//					// shift values to right by lag.
//					shiftValuesRight(Math.abs(lag));
//					adjusted = true;
//				} else if (lag<0) {
//					shiftValuesLeft(Math.abs(lag));
//					adjusted = true;
//				} else if (lag>=size()) {
//					System.err.println("Warning: computed a lag greater than the size!");
//				}
//			}
//		} else {
//			System.err.println("Warning: given a null last date for adjusting lag to!");
//		}
//	}

//	/**
//	 * will effectively move the dates of each entry forward in time by
//	 * the number of update frequencies specified.  Make sure update 
//	 * frequency is set before calling this.
//	 * @param numRight
//	 */
//	public void shiftValuesRight(int lag) {
//		shiftValues(Math.abs(lag));
//	}
	
//	/**
//	 * Will effectively move the dates of each entry backward in time
//	 * by the number of update frequencies specified.  Make sure 
//	 * update frequency is set before calling this.
//	 * @param lag
//	 */
//	public void shiftValuesLeft(int lag) {
//		shiftValues(-Math.abs(lag));
//	}
	
//	/**
//	 * will effectively move the dates of each entry forward in time by
//	 * the number of update frequencys specified.  Make sure update 
//	 * frequency is set before calling this.
//	 * @param numRight
//	 */
//	public void shiftValues(int numRight) {
//		for (E r : records) {
//			LiteDate td = r.getDate();
//			if (td!=null) {
//				td.advance(updateFrequency, numRight, true);
//			}
//		}
//	}
	
	/**
	 * does not actually do anything.
	 * @param updateFrequency
	 */
	public void adjustToFrequency(UpdateFrequency updateFrequency) {
		adjustToFrequency(updateFrequency, false);
	}
	
	/**
	 * This exists for backwards compatibility only, it does not actually 
	 * do anything.  The new record list can determine the update frequency from
	 * the values and their dates instead, so it does not make sense to set
	 * the update frequency.
	 * @param updateFrequency
	 * @param runIntegrityTest
	 */
	public void adjustToFrequency(UpdateFrequency updateFrequency, boolean runIntegrityTest) {
		// never do.
	}
	
//	/**
//	 * Will effectively change the start date.  If the current start date
//	 * is before that date, then this will remove all values before that
//	 * date.  If the current start date is after the date provided, this 
//	 * will prepend it with entries whose values are null.  This marks
//	 * adjusted true, meaning it should not be saved because the data
//	 * is permanently corrupted.
//	 */
//	public void setStartDate(LiteDate date) {
//		removeRecordsBefore(date);
//		prependNullsToDate(date);
//		adjusted = true;
//	}
	
//	/**
//	 * Will effectively change the end date.  If the current end date is
//	 * after the date provided, then this will remove all values after
//	 * that date.  If the current end date is before the date provided,
//	 * this will append entries with null values to reach that date.
//	 * This marks adjusted true, meaning it should not be saved because
//	 * the data is permanently corrupted.
//	 */
//	public void setEndDate(LiteDate date) {
//		removeRecordsAfter(date);
//		appendNullsToDate(date);
//		adjusted = true;
//	}
	
//	/**
//	 * Adjusts this to match the time frame of the interest provided.  When
//	 * done, this will have no records before the start date of the interest,
//	 * no records after the end date of the interest, and all needed records
//	 * inside those dates.  It is possible that the entries inside the dates 
//	 * will have null values though.  This sets adjusted to true, meaning it
//	 * cannot be saved, and the data is permanently corrupted.  You should 
//	 * only do this to cloned instances of the real thing.
//	 * @param interest
//	 */
//	public void adjustForTimeFrame(RecordList<? extends Record> interest) {
//		setStartDate(interest.getStartDate());
//		setEndDate(interest.getEndDate());
//	}
	
	/**
	 * Removes all the records that occured before the date provided.
	 * @param date
	 */
	public void removeRecordsBefore(LiteDate date) {
		Iterator<E> iter = records.iterator();
		E r;
		LiteDate td;
		while (iter.hasNext()) {
			r = iter.next();
			td = r.getDate();
			if (td==null || td.before(date)) {
				iter.remove();
			}
		}
		adjusted=true;
	}
	
	/**
	 * Removes all records that are after the date provided.
	 * @param date
	 */
	public void removeRecordsAfter(LiteDate date) {
		Iterator<E> iter = records.iterator();
		E r;
		LiteDate td;
		while (iter.hasNext()) {
			r = iter.next();
			td = r.getDate();
			if (td==null || td.after(date)) {
				iter.remove();
			}
		}
		adjusted=true;
	}
	
	/**
	 * Estimates the lag between this record and the date provided.  
	 * The number returned will be a multiple of update frequencies.
	 * So for example, if this is updated daily, and the most recent
	 * value is three days old, this will return 3.  If this is 
	 * updated monthly and the last value is 27 days old, this
	 * will return 0.
	 * @param interestLastDate
	 * @return
	 */
	public int estimateLag(LiteDate interestLastDate) {
		LiteDate ld = getLastDate();
		long realDelta = LiteDate.getMillisecondDelta(ld, interestLastDate);
		long maxDelta = getUpdateFrequency().getApproximateDelta();
		double d = ((double)realDelta)/((double)maxDelta);
		int lag = (int) Math.floor(d);
		return lag;
	}
	
	public int getLagInDays(LiteDate interestLastDate) {
		E r = getMostRecentOn(interestLastDate);
		LiteDate d = r.getDate();
		int lag = d.determineDaysBetween(interestLastDate);
		return lag;
	}
	
	/**
	 * Determines if the values in this list are very similar to those in the other
	 * list by judging the ratio of the values.  If the ratio of values is within 
	 * 1% of a single value most of the time, this will return true.
	 * @param otherList
	 * @return
	 */
	public MethodResponse valuesHaveConstantRatio(Collection<? extends Record> otherList) {
		double ratio = Double.MAX_VALUE;
		Iterator<E> myIter = records.iterator();
		Iterator<? extends Record> otherIter = otherList.iterator();
		Record myRecord = null;
		Record otherRecord = null;
		double myVal = 0;
		double otherVal = 0;
		double currRatio = 0;
		int timesMatching = 0;
		int timesDifferent = 0;
		double pct;
		while (myIter.hasNext() && otherIter.hasNext()) {
			myRecord = myIter.next();
			otherRecord = otherIter.next();
			myVal = myRecord.getValue();
			otherVal = otherRecord.getValue();
			if (otherVal!=0 && myVal!=0) {
				currRatio = myVal/otherVal;
				if (ratio != Double.MAX_VALUE) {
					pct = currRatio/ratio;
					if (0.99<pct && pct<1.01) {
						timesMatching++;
					} else {
						timesDifferent++;
					}
					if (timesMatching>10 && timesMatching-5>timesDifferent) {
						return new MethodResponse(true,otherList,null,"matched ten times.");
					}
					if (timesDifferent>10 && timesDifferent-5>timesMatching) {
						return new MethodResponse(false,null,null,"finished early, was " +
								"clearly different for ten times.");
					}
				}
				ratio = currRatio;
			}
		}
		return new MethodResponse(false,null,null,"iterator finished.");
	}
	
	/**
	 * Determines if the values in this list practically match the other list, 
	 * by checking that there is at least one value that is more than half a
	 * percent larger than the corresponding value in the other list.
	 * @param otherList
	 * @return
	 */
	public boolean valuesPracticallyMatch(Collection<E> otherList) {
		CodeStalker.startMethod("valuesPracticallyMatch");
		if (otherList!=null) {
			if (otherList.size()!=size()) {
				CodeStalker.finishMethod("valuesPracticallyMatch");
				return false;
			}
			
			Iterator<E> otherIter = otherList.iterator();
			E otherRecord = null;
			Double otherVal = new Double(0);
			Iterator<E> myIter = records.iterator();
			E myRecord = null;
			Double myVal = new Double(0);
			while (myIter.hasNext()) {
				otherRecord = otherIter.next();
				myRecord = myIter.next();
				otherVal = otherRecord.getValue();
				myVal = myRecord.getValue();
				if (!isPracticallyEqual(myVal, otherVal)) {
					CodeStalker.finishMethod("valuesPracticallyMatch");
					return false;
				}
			}
			CodeStalker.finishMethod("valuesPracticallyMatch");
			return true;
		} else {
			System.err.println("Warning: given a null collection to compare to. Was trying to see if values practically match.\n" +
					"  This id = "+id);
		}
		CodeStalker.finishMethod("valuesPracticallyMatch");
		return false;
	}
    
//	/**
//	 * updates this if and only if the last successful update is at least one
//	 * update frequency in the past.
//	 * @throws InterruptedException
//	 */
//	public void updateIfNecessary() throws InterruptedException {
//		LiteDate p = LiteDate.getOrCreate();
//		LiteDate minDate = p.dateOffset(updateFrequency, -1, true);
//		if (lastSuccessfulUpdate.before(minDate)) {
//			update();
//		}
//	}
	
//	@SuppressWarnings("unchecked")
//	public RecordList<E> clone() {
//		RecordList<E> rl = createInstance();
//		rl.setAdjusted(isAdjusted());
//		rl.setLastAttemptedUpdate(lastAttemptedUpdate);
//		rl.setLastSuccessfulUpdate(lastSuccessfulUpdate);
//		rl.startDate=startDate;
//		rl.endDate = endDate;
//		rl.updateFrequency=updateFrequency;
//		// don't want a reference to the same records object, that 
//		// is dangerous.
//		for (E r : records) {
//			// since some methods modify the underlying record,
//			// that must be cloned too.
//			rl.records.add((E)r.clone());
//		}
//		return rl;
//	}
	
	
	@SuppressWarnings("unused")
	private double slimDouble(double dbl) {
		double originalValue = dbl;
		String s = String.valueOf(dbl);
		int startIndex = -1;
		int countOfZeros = 0;
		int startOfZeroString = 0;
		boolean decimalFound = false;
		char c = 'a';
		int decimalPlacement = -1;
		for (int i = 0;i<s.length();i++) {
			c=s.charAt(i);
			if (startIndex<0) {
				if (c!='0' && c!='.'){
					startIndex = i;
				}
			} else if (c=='.') {
				decimalFound = true;
				decimalPlacement = i;
			} else if (c=='0') {
				countOfZeros++;
				if (countOfZeros>2 && decimalFound) {
					s = s.substring(0,Math.max(startOfZeroString+1,decimalPlacement));
					break;
				}
			} else {
				countOfZeros=0;
				startOfZeroString = i+1;
			}
		}
		if (s.contains(".")) {
			for (int i = s.length()-1;i>0;i--) {
				c=s.charAt(i);
				if (c=='0') {
					s = s.substring(0,i);
				} else {
					break;
				}
			}
		}
		if (!s.isEmpty()) {
			dbl = Double.parseDouble(s);
		} else {
			dbl = 0;
		}
		
//		if (PropertyManager.BUGFINDMODE) {
//			// Here is an integrity check: 
//			// see if the original value is within 0.5% of the new value.
//			if (originalValue!=0) {
//				double a = ((dbl/originalValue) - 1) * 100;
//				double b = Math.abs(a);
//				if (b>0.5) {
//					System.err.println("Warning: slimming a double has resulted in more" +
//							"than 0.5% change in its value.");
//				}
//			}
//		}
		return dbl;
	}
	
	/**
	 * Determines if the two values are so close that the larger is less than
	 * half a percent larger than the other.
	 * @param firstValue
	 * @param secondValue
	 * @return
	 */
	private static boolean isPracticallyEqual(double firstValue, double secondValue) {
		return (getPercentDifference(firstValue, secondValue)<0.5);
	}
	
	/**
	 * Determines the percent difference between the two values, the number
	 * returned will be greater than zero, and zero is returned when they
	 * are exactly equal.  100 is returned when one is twice as large as
	 * the other.
	 * @param value1
	 * @param value2
	 * @return
	 */
	private static double getPercentDifference(double value1, double value2) {
		double a = 0;
		if (value1==0 && value2==0) {
			return 0;
		} else if (value1==0) {
			a = 100;
		} else {
			double big = Math.max(value1, value2);
			double small = Math.min(value1, value2);
			a = ((big/small)-1)*100;
		}
		double b = Math.abs(a);
		return b;
	}
	
	@Override
	public boolean isValid() { return validateList(); }
	
	
	private boolean validateList() {
		if (isDerivative()) 
			return false;
		if (id==null || id.isEmpty()) {
			System.err.format("While trying to validate type: %s, the id was missing.",getClass().getName(),id);
			return false;
		} else {
			String problems = "";
			if (startDate==null) {
				problems += "the start date was null.\n";
			}
			if (endDate == null) {
				problems += "the end date was null.\n";
			}
			if (sourceAgency==null) {
				problems += "the source agency was null.\n";
			}
			if (updateFrequency==null) {
				problems += "the update frequency was null.\n";
			}
			if (lastSuccessfulUpdate==null) {
				problems += "the last successful update was null.\n";
			}
			if (lastAttemptedUpdate==null) {
				problems += "the last attempted update was null.\n";
			}
			if (startDate!=null && endDate!=null && !startDate.before(endDate)) {
				problems += "the start date was not before the end date.\n";
			}
			
			if (!problems.isEmpty()) {
				System.err.format("While validating the type: %s, with id: %s%n" +
						"   found the following problems: %n%s%n", getClass().getName(),id,problems);
				return false;
			}
		}
		return true;
	}
	
	public boolean isEmpty() {
		return records.isEmpty();
	}
	
	/**
	 * Gets an array of prices, if a date is missing from the
	 * history then a max value is used in its place.  The 
	 * doubles are converted into integers using the exponent.
	 * Each weekday will have a value, even if it is a holiday.
	 * 
	 * @return
	 */
	public int[] getPricesArray(byte exponent) {
		ArrayList<Integer> myList = new ArrayList<Integer>();
		LiteDate date = getStartDate();
		while (!date.after(getEndDate())) {
			Double d = getValueOnExactDate(date);
			if (d!=null) {
				Double d2 = d*Math.pow(10, exponent);
				Integer ii =  new Integer((int)Math.round(d2));
				myList.add(ii);
			} else {
				myList.add(Integer.MAX_VALUE);
			}
			date = date.dateWeekdaysAhead(1);
		}
		int[] myArray = new int[myList.size()];
		for (int i = 0;i<myList.size();i++) {
			myArray[i] = myList.get(i);
		}
		return myArray;
	}
	
	public void updateFromPricesArray(int[] prices, byte exponent,LiteDate startDate) {
		LiteDate date = startDate;
		for (int i = 0;i<prices.length;i++) {
			int val = prices[i];
			if (val!=Integer.MAX_VALUE) {
				double d = (double)val/(Math.pow(10, exponent));
				addNew(date, d);
			}
			date = date.dateWeekdaysAhead(1);
		}
	}
	
	public E getFirstRecordOnOrAfter(LiteDate date) {
		return records.getItemOnOrAfter(date);
//		
//		int index = getFirstIndexOnOrAfter(date);
//		if (index>-1) {
//			return getRecord(index);
//		}
//		return null;
	}
	
//	public int getFirstIndexOnOrAfter(LiteDate date) {
//		if (!date.before(getStartDate()) && date.before(getEndDate())) {
//			double sd = (double)getStartDate().getDate().getTimeInMillis();
//			double ed = (double)getEndDate().getDate().getTimeInMillis();
//			double dd = (double)date.getTimeInMillis();
//			double p = (dd-sd)/(ed-sd);
//			int index = (int) Math.round(p*size());
//			if (index<0) {
//				index=0;
//			} else if (index>=size()) {
//				index = size()-1;
//			}
//			E r = getRecord(index);
//			while (r.getDate().after(date)) {
//				index--;
//				if (index>=0) {
//					r = getRecord(index);
//				} else {
//					return index;
//				}
//			}
//			while (r.getDate().before(date)) {
//				index++;
//				if (index<size()) {
//					r = getRecord(index);
//				} else {
//					return index;
//				}
//			}
//			return index;
//		}
//		return -1;
//	}
	
	/**
	 * Finds the record preceding this one, which must have a non-null
	 * date and value.
	 * @param r
	 * @return
	 */
	public Record getPrecedingRecord(Record r,int minWeekdaysBefore) {
		LiteDate maxDate = r.getDate().dateWeekdaysAhead(-minWeekdaysBefore);
		Record rr = records.getItemBefore(maxDate);
		return rr;
//		int index = records.indexOf(r);
//		if (index>0) {
//			Record rr = null;
//			LiteDate maxDate = r.getDate().dateWeekdaysAhead(-minWeekdaysBefore);
//			while (rr==null || rr.getDate()==null || 
//					rr.getValue()==null || rr.getDate().after(maxDate)) {
//				index--;
//				if (index>=0) {
//					rr = records.get(index);
//				} else {
//					rr=null;
//					break;
//				}
//			}
//			return rr;
//		}
//		return null;
	}

	public Record getFollowingRecord(Record r) {
		Record rr = records.getItemAfter(r);
		return rr;
//		int index = records.indexOf(r);
//		if (index<size()-1) {
//			Record rr = null;
//			while (rr==null || rr.getDate()==null || rr.getValue()==null) {
//				index++;
//				if (index<size()) {
//					rr = records.get(index);
//				} else {
//					rr=null;
//					break;
//				}
//			}
//			return rr;
//		}
//		return null;
	}

	public String[][] getEntriesArray() {
		String[][] entries = new String[records.size()][2];
		for (int i = 0;i<records.size();i++) {
			Record r = records.getItem(i);
			if (r!=null) {
				LiteDate d = r.getDate();
				if (d!=null) {
					entries[i][0] = d.toString();
				} else {
					entries[i][0] = "null";
				}
				Double v = r.getValue();
				entries[i][1] = String.valueOf(v);
			} else {
				entries[i][0]="err";
				entries[i][1]="err";
			}
		}
		return entries;
	}
	
	public int countZeros() {
		int sum = 0;
		for (Record r : this.records) {
			if (r==null || r.getValue()==null || r.getValue()==0) {
				sum++;
			}
		}
		return sum;
	}
	
	public boolean isMostlyZeros() {
		if (size()>0) {
			return (((double)countZeros()/(double)size())>0.5);
		}
		return true;
	}
	
	public void incrementTimesUsedInFinalRegression() {
		this.timesUsedInFinalRegressionResults++;
	}
	
	public int getTimesUsedInFinalRegression() {
		return this.timesUsedInFinalRegressionResults;
	}
	
	public void resetTimesUsedInFinalRegression() {
		this.timesUsedInFinalRegressionResults=0;
	}
	
	public int validNonZeroEntriesInTimeFrame(TimeFrame timeFrame) {
		int sum=0;
		for (E r : this.records) {
			if (r!=null && r.getDate()!=null && r.getValue()!=null && 
					!r.getValue().equals(new Double(0))) {
				LiteDate d = r.getDate();
				if (timeFrame.includes(d)) {
					sum++;
				}
			}
		}
		return sum;
	}

	@Override
	public TimeFrame getTimeFrame() {
		return new TimeFrame(getStartDate(),getEndDate());
	}
	
	public final synchronized boolean isLocked() {
		return lock.isLocked();
	}
	
	public final synchronized boolean canEdit() {
		return lock.canEdit();
	}
	
	public final synchronized void lock() {
		records.indexArray();
		lock.lock();
	}
	
	public final synchronized void unlock() {
		lock.unlock();
	}
	
	public final synchronized void permanentlyLock() {
		records.indexArray();
		lock.permanentlyLock();
	}
	
	public final boolean isDerivative() {
		return derivative;
	}
	
	public final synchronized RecordList<E> getDerivative() {
		if (!isDerivative()) {
			if (derivativeMade!=null) 
				return derivativeMade;
			derivativeMade = this.clone();
			if (derivativeMade==null) {
				System.out.println("cloning this returned null!");
			}
			derivativeMade.derivative=true;
			derivativeMade.clear();
			if (records==null) {
				System.out.println("records are null while trying to make a derivative!");
			}
			PredictableIndexArray<E> rs = (PredictableIndexArray<E>)records.clone();
			E r1;
			E r2;
			rs.indexArray();
			for (int i = 1;i<rs.size();i++) {
				r1 = rs.getItem(i-1);
				r2 = rs.getItem(i);
				derivativeMade.addOrReplace(r2.getDate(), r2.getValue()-r1.getValue());
			}
			derivativeMade.permanentlyLock();
			return derivativeMade;
		} else {
			throw new IllegalStateException("You are not allowed to create " +
					"a derivative of a derivative.");
		}
	}
	
	public final List<E> getRecords() {
		return Collections.unmodifiableList(new ArrayList<E>(this.records));
	}
	
	public final void clear() {
		if (!isLocked()) {
			this.records.clear();
			startDate=null;
			endDate = null;
			updateFrequency=null;
		}
	}
	
	public abstract RecordList<E> clone();
    public abstract E createRecord(LiteDate date,double value);
}