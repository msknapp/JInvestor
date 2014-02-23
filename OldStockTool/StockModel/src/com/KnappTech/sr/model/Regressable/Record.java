package com.KnappTech.sr.model.Regressable;

import com.KnappTech.model.LiteDate;

/**
 * A record of a date and value.
 * @author Michael Knapp
 */
public class Record implements Comparable<Record>, Cloneable {
	private final LiteDate date; // impossible to be null.
	private final Double value; // impossible to be null.
	
	private Record(LiteDate date, double value) {
		this.date = date;
		this.value=value;
	}
	
	public static final Record create(LiteDate date,double value) {
		if (date!=null) {
			return new Record(date,value);
		} else {
			throw new IllegalArgumentException("Cannot create a record with date: "+
					date+", and value: "+value);
		}
	}
	
	public LiteDate getDate() {
		return date;
	}
	
	public Double getValue() {
		return value;
	}
	
	public boolean equals(Object o) {
		if (o==null) 
			return false;
		if (!(o instanceof Record)) 
			return false;
		return ((Record)o).getDate().equals(getDate());
	}
	
	public int hashCode() {
		return date.hashCode();
	}
	
	public String toString() {
		return date.toString()+": "+value+"\n";
	}

	@Override
	public int compareTo(Record r) {
		return date.compareTo(r.getDate());
	}
}