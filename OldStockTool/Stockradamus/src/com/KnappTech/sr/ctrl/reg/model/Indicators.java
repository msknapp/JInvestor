package com.KnappTech.sr.ctrl.reg.model;

import java.util.Collection;
import java.util.Hashtable;

import com.KnappTech.sr.model.comp.Indicator;

public class Indicators implements IIndicators {
	private final Hashtable<String,Indicator> items = new Hashtable<String,Indicator>();
	
	public Indicators() {
		
	}

	@Override
	public String getWorstID() {
		if (items.size()<1)
			throw new IllegalStateException("Cannot get worst id until items have been set.");
		String worstID = null;
		double biggestError = -1;
		double itemError = -1;
		for (Indicator item : items.values()) {
			if (item==null)
				continue;
			itemError = item.getPercentFactorError();
			if (itemError<0)
				throw new IllegalStateException("Found an indicator with negative induced error.");
			if (itemError<biggestError)
				continue;
			biggestError = itemError;
			worstID = item.getID();
		}
		return worstID;
	}
	
	public void add(Indicator item) {
		items.put(item.getID(),item);
	}
	
	public void addAll(Collection<Indicator> items) {
		for (Indicator ind : items) 
			this.items.put(ind.getID(),ind);
	}
	
	public Collection<Indicator> getItems() {
		return items.values();
	}

	@Override
	public Collection<Indicator> getIndicators() {
		return items.values();
	}
}