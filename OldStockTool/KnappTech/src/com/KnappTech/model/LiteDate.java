package com.KnappTech.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class LiteDate implements Serializable, Cloneable, 
Comparable<LiteDate>, Identifiable<LiteDate> {
	public static final long serialVersionUID = 201011182150L;
	public static final int BASEYEAR = 1980;
	public static final LiteDate EPOCH = new LiteDate(BASEYEAR,Calendar.JANUARY,1);
	public static final LiteDate present = new LiteDate(Calendar.getInstance());
	public static final String TOSTRINGFORMAT = "MMM d, yyyy";
	
	
	
	private final byte tYear; // 0 = 1980, 20= 2000, etc.
	private final byte tMonth; // 0 = January, 11 = December.
	private final byte tDay; // 1 to 31.
	
	private LiteDate() {
		this(Calendar.getInstance());
	}
	
	private LiteDate(int tYear, int tMonth, int tDay){
		this.tYear = (byte)(tYear-BASEYEAR);
		this.tMonth = (byte)tMonth;
		this.tDay = (byte)tDay;
	}
	
	private LiteDate(Calendar gc){
		this.tYear = (byte)(gc.get(Calendar.YEAR)-BASEYEAR);
		this.tMonth = (byte)gc.get(Calendar.MONTH);
		this.tDay = (byte)gc.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * Creates a lite date offset from the present by some amount,
	 * for example:
	 * create(Calendar.MONTH,-1);
	 * would return a litedate that is exactly one month in the past.
	 * @param field
	 * @param offset
	 * @return
	 */
	public static final LiteDate getOrCreate(int field,int offset) {
		initializeOnceOnly();
		Calendar c2 = Calendar.getInstance();
		c2.add(field, offset);
		return getOrCreate(c2);
	}
	public static final LiteDate getOrCreate() {
		initializeOnceOnly();
		Calendar present = Calendar.getInstance();
		return getOrCreate(present);
	}
	
	public static final LiteDate getOrCreate(Calendar cal,int field,int offset) {
		initializeOnceOnly();
		Calendar c2 = (Calendar) cal.clone();
		c2.add(field, offset);
		return getOrCreate(c2);
	}
	
	public static final LiteDate getOrCreate(int tYear, byte tMonth, byte tDay) {
		initializeOnceOnly();
		if (BASEYEAR<=tYear && tYear<=2100 && 
				Calendar.JANUARY<=tMonth && tMonth<=Calendar.DECEMBER && 
				1<=tDay && tDay<=31) {
			LiteDate d = LiteDates.get(tYear,tMonth,tDay);
			if (d!=null) 
				return d;
			// from here down we know that LiteDates does not already have this date.
			if (tDay>28) {
				byte dim = 31;
				if (tMonth == Calendar.APRIL ||
						tMonth == Calendar.JUNE || tMonth == Calendar.SEPTEMBER ||
						tMonth == Calendar.NOVEMBER) {
					dim=30;
				}
				if (tMonth==Calendar.FEBRUARY) {
					if (tYear%4==0) {
						dim=29;
					} else {
						dim=28;
					}
				}
				if (tDay>dim) {
					throw new IllegalArgumentException("Given an impossible day considering "
							+"the month and year: "+tYear+", "+tMonth+", "+tDay);
				}
			}
			d = new LiteDate(tYear,tMonth,tDay);
			// add will not add a duplicate.
			LiteDates.add(d);
			return d;
		} else {
			throw new IllegalArgumentException("Could not create a date with "
					+"the arguments provided: "+tYear+", "+tMonth+", "+tDay);
		}
	}
	
	public static final LiteDate getOrCreate(Calendar cal) {
		return getOrCreate(cal.get(Calendar.YEAR), (byte)cal.get(Calendar.MONTH), 
				(byte)cal.get(Calendar.DAY_OF_MONTH));
	}
	
	public static final LiteDate getOrCreate(String value,String format) {
		try {
			initializeOnceOnly();
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Calendar cl = Calendar.getInstance();
			cl.setTime(sdf.parse(value));
			int y = cl.get(Calendar.YEAR);
			if (y<BASEYEAR && !format.contains("yyy")) {
				y+=100;
			}
			return getOrCreate(cl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static final void initializeOnceOnly() {
		if (LiteDates.isInitialized())
			return;
		System.out.println("Starting lite dates initialization.");
		ArrayList<LiteDate> ds = new ArrayList<LiteDate>();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, LiteDate.BASEYEAR);
		c.set(Calendar.MONTH,Calendar.JANUARY);
		c.set(Calendar.DAY_OF_MONTH, 1);
		Calendar present = Calendar.getInstance();
		while (!c.after(present)) {
			ds.add(new LiteDate(c));
			c.add(Calendar.DAY_OF_YEAR, 1);
		}
		LiteDates.addAll(ds);
		System.out.println("Finished lite dates initialization.");
	}
	
	public Calendar getDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, getYear());
		cal.set(Calendar.MONTH, getMonth());
		cal.set(Calendar.DAY_OF_MONTH, getDay());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	public int determineDaysBetween(LiteDate otherDate) {
		long firstTime = getDate().getTimeInMillis();
		long secondTime = otherDate.getDate().getTimeInMillis();
		long delta = firstTime-secondTime;
		double dBetween = Math.abs(((double)delta)/(86400000));
		return (int)Math.round(dBetween);
	}
	
	public int determineWeeksBetween(LiteDate otherDate) {
        double weeksBetween = 9999;
        weeksBetween = Math.abs((getDate().getTimeInMillis()-otherDate.getDate().getTimeInMillis())/(604800000));
        return (int)Math.round(weeksBetween);
    }
	
	public static int determineWeeksBetween(LiteDate startDate, LiteDate endDate) {
        double weeksBetween = 9999;
        weeksBetween = Math.abs((startDate.getDate().getTimeInMillis()-endDate.getDate().getTimeInMillis())/(604800000));
        return (int)Math.round(weeksBetween);
    }
	
	public boolean before(LiteDate otherDate){
		if (otherDate == null)
			throw new NullPointerException();
		return hashCode()<otherDate.hashCode();
	}
	
	public boolean onOrBefore(LiteDate otherDate) {
		return !after(otherDate);
	}

	public boolean after(LiteDate otherDate){
		if (otherDate == null){
			return true; // my convention.
		}
		return (hashCode()>otherDate.hashCode());
	}
	
	public boolean onOrAfter(LiteDate otherDate) {
		return !before(otherDate);
	}
	
	public byte getDay(){
		return tDay;
	}
	
	public byte getMonth(){
		return tMonth;
	}
	
	public int getYear(){
		return tYear+BASEYEAR;
	}
	
	public byte getByteYear(){
		return tYear;
	}
	
	public static LiteDate getDateFromString(String sDate){
		if (!sDate.equals("")){
			int si = 0;
			int ei = sDate.length();
			int count = 0;
			String s = "";
			int[] myArr = new int[3];
			for (int i=0;i<sDate.length();i++){
				if (sDate.charAt(i)=='/') {
					ei = i;
					s = sDate.substring(si, ei);
					si = i+1;
					myArr[count]=Integer.parseInt(s);
					count++;
				}
			}
			ei = sDate.length();
			s = sDate.substring(si, ei);
			myArr[count]=Integer.parseInt(s);
			return new LiteDate(myArr[2], myArr[0]-1, myArr[1]);
		} else {
			return new LiteDate(1980,0,1);
		}
	}
	
	@Override
	public boolean equals(Object trendDate){
		if (trendDate==null) 
			return false;
		if (!(trendDate instanceof LiteDate))
			return false;
		return hashCode()==((LiteDate)trendDate).hashCode();
	}

	@Override
	public int hashCode() {
		return determineHashCode(tDay, tMonth, tYear);
	}
	
	/**
	 * Determines the hash code for the date provided, the year 
	 * must have already subtracted the base year.
	 * @param day
	 * @param month
	 * @param year
	 * @return
	 */
	public static final int determineHashCode(byte day, byte month, byte year) {
		return day+month*31+year*372;
	}
	
	/**
	 * Determines the hash code for the date provided, the year should NOT
	 * have already subtracted the base year.  This operation will subtract
	 * the base year first.
	 * @param day
	 * @param month
	 * @param year
	 * @return
	 */
	public static final int determineHashCode(int day, int month, int year) {
		return day+month*32+(year-BASEYEAR)*366;
	}
	
	public LiteDate dateOffset(int field,int offset) {
		Calendar c = getDate();
		c.add(field, offset);
		return getOrCreate(c);
	}
	
	public LiteDate dateMonthsAhead(int offset) {
		return (dateOffset(Calendar.MONTH, offset));
	}
	
	public LiteDate dateWeeksAhead(int offset) {
		return (dateOffset(Calendar.WEEK_OF_YEAR, offset));
	}
	
	public LiteDate dateDaysAhead(int offset) {
		return (dateOffset(Calendar.DAY_OF_MONTH, offset));
	}
	
	public LiteDate dateOffSet(UpdateFrequency uf,int offset) {
		return dateOffset(uf,offset,true);
	}
//	public void advance(UpdateFrequency uf, int num) {
//		advance(uf,num,true);
//	}
//	
	
	public LiteDate dateOffset(UpdateFrequency uf,int offset,boolean treatDailyAsWeekdays) {
		if (uf.equals(UpdateFrequency.DAILY)) {
			if (treatDailyAsWeekdays) {
				return dateWeekdaysAhead(offset);
			} else {
				return dateOffset(Calendar.DAY_OF_MONTH,offset);
			}
		} else if (uf.equals(UpdateFrequency.WEEKLY)) {
			return dateOffset(Calendar.WEEK_OF_YEAR,offset);
		} else if (uf.equals(UpdateFrequency.BIWEEKLY)) {
			return dateOffset(Calendar.WEEK_OF_YEAR,offset*2);
		} else if (uf.equals(UpdateFrequency.MONTHLY)) {
			return dateOffset(Calendar.MONTH,offset);
		} else if (uf.equals(UpdateFrequency.BIMONTHLY)) {
			return dateOffset(Calendar.MONTH,offset*2);
		} else if (uf.equals(UpdateFrequency.QUARTERLY)) {
			return dateOffset(Calendar.MONTH,offset*3);
		} else if (uf.equals(UpdateFrequency.SEMIANNUALLY)) {
			return dateOffset(Calendar.MONTH,offset*6);
		} else if (uf.equals(UpdateFrequency.ANNUALLY)) {
			return dateOffset(Calendar.YEAR,offset);
		} else {
			System.err.println("advancing by the update frequency "+uf.name()+" is not " +
					"a supported operation by the trend date.");
		}
		return null;
	}

	public LiteDate dateWeekdaysAhead(int days) {
		Calendar gc = this.getDate();
		int weeks = 0;
		if (days>=0) {
			weeks = (int)Math.floor(days/5);
		} else {
			weeks = (int) -Math.floor((5-days)/5);
		}
		gc.add(Calendar.WEEK_OF_YEAR, weeks);
		int count = 0;
		if (days>=0) {
			count = days%5;
		} else {
			count = ((days-5*weeks)%5);
		}
		int dayOfWeek = gc.get(Calendar.DAY_OF_WEEK);
		gc.add(Calendar.DAY_OF_MONTH, count);
		if (dayOfWeek+count>6) {
			gc.add(Calendar.DAY_OF_MONTH, 2);
		} else if (dayOfWeek + count < 2) {
			gc.add(Calendar.DAY_OF_MONTH, -2);
		}
		return LiteDate.getOrCreate(gc);
	}
	
//	public void advanceWeeks(int weeks){
//		LiteDate dDate = this.addWeeks(weeks);
//		setDate(dDate.getYear(),dDate.getMonth(),dDate.getDay());
//	}
//	
//	public void advanceMonths(int months){
//		LiteDate dDate = this.addMonths(months);
//		setDate(dDate.getYear(),dDate.getMonth(),dDate.getDay());
//	}
	
//	public void advanceYears(int years){
//		LiteDate dDate = this.addYears(years);
//		setDate(dDate.getYear(),dDate.getMonth(),dDate.getDay());
//	}
	
	public LiteDate add(int val, String interval){
		return add(this,val,interval);
	}
	
	public static LiteDate add(LiteDate dDate, int val, String interval){
		if (dDate != null){
			if (interval.toLowerCase().contains("year")){
				return dDate.addYears(val);
			} else if (interval.toLowerCase().contains("month")){
				return dDate.addMonths(val);
			} else { // we assume weeks by default.
				return dDate.addWeeks(val);
			} 
		} else {
			return null;
		}
	}

	public LiteDate addDays(int days){
		Calendar gc = this.getDate();
		gc.add(Calendar.DATE, days);
		return new LiteDate(gc);
	}
	
	public LiteDate addWeeks(int weeks){
		Calendar gc = this.getDate();
		gc.add(Calendar.WEEK_OF_YEAR, weeks);
		return new LiteDate(gc);
	}
	
	public LiteDate addMonths(int months){
		Calendar gc = this.getDate();
		gc.add(Calendar.MONTH, months);
		return new LiteDate(gc);
	}
	
	public LiteDate addYears(int years){
		Calendar gc = this.getDate();
		gc.add(Calendar.YEAR, years);
		LiteDate td = new LiteDate(gc);
		return td;
	}
	
	public static LiteDate min(LiteDate d1, LiteDate d2){
		if (d1 != null && d2 != null){
			if (d1.after(d2)){
				return d2;
			} else {
				return d1;
			}
		} else {
			if (d1 != null){
				return d1;
			} else if (d2 != null){
				return d2;
			} else {
				return null;
			}
		}
	}
	
	public static LiteDate max(LiteDate d1, LiteDate d2){
		if (d1 != null && d2 != null){
			if (d1.before(d2)){
				return d2;
			} else {
				return d1;
			}
		} else {
			if (d1 != null){
				return d1;
			} else if (d2 != null){
				return d2;
			} else {
				return null;
			}
		}
	}
	
//	public String asString(){
//		String s = Util.makeIntAStringOfLength((tMonth+1),2) + "/" + 
//				Util.makeIntAStringOfLength(tDay,2) + "/" + 
//				Util.makeIntAStringOfLength((tYear+1980),2);
//		return s;
//	}
	
	public String asNumString(){
		String s1;
		if (tMonth+1>=10){
			s1 = String.valueOf(tMonth+1);
		} else {
			s1 = "0"+String.valueOf(tMonth+1);
		}
		String s2;
		if (tDay>=10){
			s2 = String.valueOf(tDay+1);
		} else {
			s2 = "0"+String.valueOf(tDay+1);
		}
		return s1+s2 + String.valueOf(tYear+1980);
	}
	
//	public LiteDate clone(){
//		return new LiteDate(tYear+BASEYEAR,tMonth,tDay);
//	}
	
	public LiteDate getPriorFriday(){
		return getPriorWeekDay(this,6);
	}
	
    public static LiteDate getPriorFriday(LiteDate dDate){
    	return getPriorWeekDay(dDate,6);
    }
    
    public LiteDate getPriorMonday(){
    	return getPriorWeekDay(this,2);
    }
    
    public LiteDate getPriorSunday(){
    	return getPriorWeekDay(this,1);
    }

    public static LiteDate getPriorSunday(LiteDate dDate){
    	return getPriorWeekDay(dDate,1);
    }
    
    public static LiteDate getPriorMonday(LiteDate dDate){
    	return getPriorWeekDay(dDate,2);
    }
    
    public LiteDate getMostRecentTradingDay(){
    	return getMostRecentTradingDay(this);
    }
    
    public static LiteDate getMostRecentTradingDay(LiteDate dDate){
    	if (dDate != null){
	    	Calendar gc = Calendar.getInstance();
	    	gc.set(Calendar.YEAR, dDate.getYear());
	    	gc.set(Calendar.MONTH, dDate.getMonth());
	    	gc.set(Calendar.DAY_OF_MONTH, dDate.getDay());
    		int weekDay = gc.get(Calendar.DAY_OF_WEEK);
	    	if (2<=weekDay && weekDay<=6){
	    		return dDate;
	    	} else { // if (weekDay<=1){
	    		return getPriorFriday(dDate);
	    	} // else { // if (weekDay>=7) {
    	}
    	return null;
    }
    
    /**
     * The week day before its own date.  if it is a Tuesday,
     * this returns yesterday, if it is a Monday, this will
     * return the prior Friday.
     * @return
     */
    public LiteDate getPriorWeekDay() {
    	return dateWeekdaysAhead(-1);
    }
    
    public LiteDate getPriorWeekDay(int desiredWeekDay){
    	return getPriorWeekDay(this,desiredWeekDay);
    }
    
    public static LiteDate getPriorWeekDay(LiteDate dDate, int desiredWeekDay){
    	// weekdays go from 1 to 7, Sun-Sat.
    	if (dDate != null){
    		if (desiredWeekDay<1){
    			desiredWeekDay = 1;
    		}
    		if (7<desiredWeekDay){
    			desiredWeekDay = 7;
    		}
	    	Calendar gc = Calendar.getInstance();
	    	gc.set(Calendar.YEAR, dDate.getYear());
	    	gc.set(Calendar.MONTH, dDate.getMonth());
	    	gc.set(Calendar.DAY_OF_MONTH, dDate.getDay());
	    	int weekDay = gc.get(Calendar.DAY_OF_WEEK);
	    	if (weekDay>desiredWeekDay) {
	    		gc.add(Calendar.DAY_OF_WEEK, desiredWeekDay-weekDay);
	    	} else if (weekDay<desiredWeekDay) {
	    		gc.add(Calendar.DAY_OF_WEEK, -weekDay-(7-desiredWeekDay));
	    	}
	    	LiteDate td = new LiteDate(gc);
	    	return td;
    	} else {
    		return null;
    	}
    }
    
    public static String getLongTimeString(){
    	String s = "";
    	Calendar gc = Calendar.getInstance();
    	
    	s = String.valueOf(gc.get(Calendar.MONTH))+
    		String.valueOf(gc.get(Calendar.DAY_OF_MONTH))+
    		String.valueOf(gc.get(Calendar.YEAR))+"_"+
    		String.valueOf(gc.get(Calendar.HOUR))+
    		String.valueOf(gc.get(Calendar.MINUTE))+
    		String.valueOf(gc.get(Calendar.SECOND));
    	return s;
    }
    
    public static String getMonthYearString(){
    	String s = "";
    	Calendar gc = Calendar.getInstance();
    	
    	s = String.valueOf(gc.get(Calendar.YEAR));
    	int month = gc.get(Calendar.MONTH);
    	if (month <10){
    		s += "0" + String.valueOf(month);
    	} else {
    		s += String.valueOf(month);
    	}
    	
    	return s;
    }
    
    
    public LiteDate getMonthStartDate(){
    	return getMonthStartDate(this);
    }
    
    
    public static LiteDate getMonthStartDate(LiteDate dDate){
    	if (dDate != null){
	    	int ff = dDate.getFirstFridayOfMonth();
	    	if (ff<=dDate.getDay()){
	    		return new LiteDate(dDate.getYear(),dDate.getMonth(),ff);
	    	} else {
	    		LiteDate tDate = dDate.addWeeks(-1);
	    		ff = dDate.getFirstFridayOfMonth();
	    		return new LiteDate(tDate.getYear(),tDate.getMonth(),ff);
	    	}
    	} else {
    		return null;
    	}
    }
    
    public int getFirstFridayOfMonth(){
    	return getFirstFridayOfMonth(this);
    }
    
    
    public static int getFirstFridayOfMonth(LiteDate dDate){
    	// Assumes Friday is what you want.
		// assume that week goes from 1 to 7, so there is some rollover.
		// Sunday = 1, Saturday = 7
    	if (dDate != null){
        	Calendar gc = Calendar.getInstance();
        	gc.set(Calendar.YEAR, dDate.getYear());
        	gc.set(Calendar.MONTH, dDate.getMonth());
        	gc.set(Calendar.DAY_OF_MONTH, 1);
			int dayofweek = gc.get(Calendar.DAY_OF_WEEK); // the integer representing day of week of the first day of the month.
			int firstFridayOfMonth = 1;
			if (dayofweek<5) { // the first day is included in the official first week
				firstFridayOfMonth = 7-dayofweek;
			} else { // the first week is later.
				firstFridayOfMonth = 14-dayofweek;
			}
			return firstFridayOfMonth;
    	} else {
    		return 0;
    	}
    }
    
    public LiteDate getDateOfWeek(int week){
    	return getDateOfWeek(this,week);
    }
    
    
    public static LiteDate getDateOfWeek(LiteDate dDate,int week){
		// Assumes Friday is what you want.
		// assume that week goes from 1 to 7, so there is some rollover.
		// Sunday = 1, Saturday = 7
    	if (dDate != null && 1<=week && week <= 7){
			int firstFridayOfMonth = getFirstFridayOfMonth(dDate);
	    	Calendar gc = Calendar.getInstance();
			gc.set(dDate.getYear(), dDate.getMonth(), firstFridayOfMonth);
			gc.add(Calendar.DAY_OF_MONTH, 7*(week-1));
			return new LiteDate(gc);
    	} else if (dDate != null) {
    		return dDate;
    	} else {
    		return null;
    	}
	}
    
    public static LiteDate parseFromDouble(double td){
    	String std = String.valueOf(td);
    	if (std.length()==6){
    		int i1 = Integer.parseInt(std.substring(0, 2));
    		int i2 = Integer.parseInt(std.substring(2, 4));
    		int i3 = Integer.parseInt(std.substring(4, 6));
	    	LiteDate tDate = new LiteDate(i1,i2,i3);
	    	return tDate;
    	}
    	return null;
    }

	@Override
	public int compareTo(LiteDate o) {
		return (hashCode()-o.hashCode());
	}
	
	public static LiteDate getNextWeekday(LiteDate date) {
		LiteDate pdate = date;
		while (!isWeekday(pdate)){
			pdate = pdate.dateDaysAhead(1);
		}
		return pdate;
	}
	
	public static LiteDate getNextTradingDate(LiteDate date) {
		LiteDate pdate = date;
		while (!isMarketOpen(pdate)){
			pdate = pdate.dateDaysAhead(1);
		}
		return pdate;
	}
	
	public static boolean isWeekday(LiteDate date) {
		Calendar cal = date.getDate();
		return isWeekday(cal);
	}
	
	public static boolean isWeekday(Calendar cal) {
		int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
		if (dayofweek == 1 || dayofweek == 7) {
			return false;
		}
		return true;
	}
	
	public static boolean isMarketOpen(LiteDate date) {
		if (isWeekday(date)){
			return !isMarketHoliday(date);
		}
		return false; 
	}

	private static boolean isMarketHoliday(LiteDate date) {
		// TODO finish determining if it is a market holiday.
		switch (date.tMonth) {
			case 0: {
				if (date.tDay==1) {
					return true;
				}
			}
			break;
			case 1: { // check if it's president's day. third monday of february
				if (15<=date.tDay && date.tDay<=21){
					LiteDate presidentsDay = new LiteDate(date.tYear,1,15);
					while (presidentsDay.getDate().get(Calendar.DAY_OF_WEEK)!=2) {
						presidentsDay.addDays(1);
					}
					return date.equals(presidentsDay);
				}
			}
			break;
			case 3: { // check if it's good friday
				
			}
			break;
			case 4: { // Check for memorial day
				
			}
			break;
			case 6: { // Check for independence day
				if (3<=date.tDay && date.tDay <= 5){
					LiteDate independenceDay = new LiteDate(date.tYear,(byte)6,4);
					LiteDate observedIndependenceDay = new LiteDate(date.tYear,(byte)6,4);
					Calendar cal = independenceDay.getDate();
					int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
					if (dayofweek == 1) {
						observedIndependenceDay = new LiteDate(date.tYear,(byte)6,5);
					} else if (dayofweek == 7) {
						observedIndependenceDay = new LiteDate(date.tYear,(byte)6,3);
					}
					return date.equals(observedIndependenceDay);
				}
			}
			break;
			case 8: { // Labor day
				
			}
			break;
			case 10: { // Thanksgiving
				
			}
			break;
			case 11: { // Christmas
				if (24<=date.tDay && date.tDay<=26){
					LiteDate christmasDay = new LiteDate(date.tYear,(byte)11,25);
					LiteDate observedChristmasDay = new LiteDate(date.tYear,(byte)11,25);
					Calendar cal = christmasDay.getDate();
					int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
					if (dayofweek == 1) {
						observedChristmasDay = new LiteDate(date.tYear,(byte)11,26);
					} else if (dayofweek == 7) {
						observedChristmasDay = new LiteDate(date.tYear,(byte)11,24);
					}
					return date.equals(observedChristmasDay);
				}
			}
		}
		return false;
	}
	
	public static long getMillisecondDelta(LiteDate first, LiteDate second) {
		Calendar f = first.getDate();
		Calendar s = second.getDate();
		return s.getTimeInMillis()-f.getTimeInMillis();
	}
	
	public String getFormatted(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String formattedDateString = sdf.format(getDate().getTime());
		return formattedDateString;
	}
	
	@Override
	public String toString() {
		String format = TOSTRINGFORMAT;
		return getFormatted(format);
	}
	
	public long getTimeInMillis() {
		return getDate().getTimeInMillis();
	}
	
	public int getDaysInMonth() {
		if (getYear()%4==0 && getMonth()==Calendar.FEBRUARY) {
			return 29;
		}
		if (tMonth==Calendar.JANUARY || 
			tMonth==Calendar.MARCH || 
			tMonth==Calendar.MAY || 
			tMonth==Calendar.JULY || 
			tMonth==Calendar.AUGUST || 
			tMonth==Calendar.OCTOBER || 
			tMonth==Calendar.DECEMBER) {
			return 31;
		}
		if (getMonth()==Calendar.FEBRUARY) {
			return 28;
		}
		return 30;
	}

	@Override
	public LiteDate getID() {
		return this;
	}
}
