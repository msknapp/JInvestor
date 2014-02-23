package com.KnappTech.model;

import java.util.Calendar;
import java.util.Collection;

public class LiteDates {
	private final LiteDate[][][] dates = initializeDates();
	private static final LiteDates instance = new LiteDates();
	private static boolean initialized = false;
	
	private LiteDates(){}
	
	public static final LiteDate getOrCreate() {
		initializeOnceOnly();
		Calendar cal = Calendar.getInstance();
		return getOrCreate(cal);
	}
	
	private static final LiteDate[][][] initializeDates() {
		return new LiteDate[(Calendar.getInstance()).get(Calendar.YEAR)+1-LiteDate.BASEYEAR][12][31];
	}
	
	public static final LiteDate getOrCreate(Calendar cal) {
		// the lite date manages updating this list during creation.
		initializeOnceOnly();
		return LiteDate.getOrCreate(cal);
	}
	
	public static final LiteDate getOrCreate(int field,int offset) {
		initializeOnceOnly();
		return LiteDate.getOrCreate(field, offset);
	}
	
	public static final LiteDate getOrCreate(Calendar cal,int field,int offset) {
		initializeOnceOnly();
		return LiteDate.getOrCreate(cal, field, offset);
	}
	
	public static final LiteDate getOrCreate(int tYear, byte tMonth, byte tDay) {
		initializeOnceOnly();
		return LiteDate.getOrCreate(tYear, tMonth, tDay);
	}
	
	private static final void initializeOnceOnly() {
		if (!initialized) 
			LiteDate.initializeOnceOnly();
		initialized = true;
	}
	
	// not private, so LiteDate can add them.
	static final boolean add(LiteDate d) {
		return instance.iAdd(d);
	}
	
	static final boolean addAll(Collection<LiteDate> ds) {
		if (ds.size()>0)
			initialized=true;
		return instance.iAddAll(ds);
	}
	
	private synchronized final boolean iAddAll(Collection<LiteDate> ds) {
		boolean pass = true;
		for (LiteDate d : ds) {
			if (d!=null && !iHas(d)) {
				dates[d.getByteYear()][d.getMonth()][d.getDay()-1]=d;
			} else {
				pass=false;
			}
		}
		return pass;
	}
	
	private synchronized final boolean iAdd(LiteDate d) {
		if (d!=null && !iHas(d)) {
			dates[d.getByteYear()][d.getMonth()][d.getDay()-1]=d;
			return true;
		}
		return false;
	}
	
	static final LiteDate get(LiteDate d) {
		return instance.iGet(d);
	}
	
	private synchronized final LiteDate iGet(LiteDate d) {
		return dates[d.getByteYear()][d.getMonth()][d.getDay()-1];
	}
	
	private static final boolean has(LiteDate d) {
		return instance.iHas(d);
	}
	
	private synchronized final boolean iHas(LiteDate d) {
		return (iGet(d)!=null);
	}
	
	public static synchronized final boolean isInitialized() {
		return initialized;
	}

	public static final boolean has(int tYear, byte tMonth, byte tDay) {
		return instance.iHas(tYear,tMonth,tDay);
	}

	public static final LiteDate get(int tYear, byte tMonth, byte tDay) {
		return instance.iGet(tYear,tMonth,tDay);
	}
	
	private synchronized final LiteDate iGet(int tYear, byte tMonth, byte tDay) {
		return dates[tYear-LiteDate.BASEYEAR][tMonth][tDay-1];
	}
	
	private synchronized final LiteDate iGet(byte tYear, byte tMonth, byte tDay) {
		return dates[tYear][tMonth][tDay-1];
	}
	
	private synchronized final boolean iHas(int tYear, byte tMonth, byte tDay) {
		return iGet(tYear,tMonth,tDay)!=null;
	}
	
	private synchronized final boolean iHas(byte tYear, byte tMonth, byte tDay) {
		return iGet(tYear,tMonth,tDay)!=null;
	}
}