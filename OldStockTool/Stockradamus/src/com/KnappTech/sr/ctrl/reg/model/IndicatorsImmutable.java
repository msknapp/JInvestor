package com.KnappTech.sr.ctrl.reg.model;

import java.util.Collection;
import java.util.Collections;

import com.KnappTech.sr.model.comp.Indicator;

public class IndicatorsImmutable implements IIndicators {
	private final Indicators items;
	
	public IndicatorsImmutable(Indicators items) {
		this.items = new Indicators();
		this.items.addAll(items.getItems());
	}

	@Override
	public String getWorstID() {
		return items.getWorstID();
	}

	@Override
	public Collection<Indicator> getIndicators() {
		return Collections.unmodifiableCollection(items.getIndicators());
	}
}