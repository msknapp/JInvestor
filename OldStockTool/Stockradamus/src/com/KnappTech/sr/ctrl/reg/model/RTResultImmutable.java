package com.KnappTech.sr.ctrl.reg.model;

import com.KnappTech.sr.model.constants.RegressionMethod;

public class RTResultImmutable implements IRTResult {
	private final RTResult result;
	
	public RTResultImmutable(RTResult result) {
		this.result = new RTResult();
		this.result.setCoefficentOfDetermination(result.getCoefficentOfDetermination());
		this.result.setRating(result.getRating());
		this.result.setRegressionMethod(result.getRegressionMethod());
		this.result.setTargetID(result.getTargetID());
		this.result.setVariance(result.getVariance());
		this.result.setItems(result.getItems());
	}

	@Override
	public double getVariance() {
		return result.getVariance();
	}

	@Override
	public double getCoefficentOfDetermination() {
		return result.getCoefficentOfDetermination();
	}

	@Override
	public double getRating() {
		return result.getRating();
	}

	@Override
	public String getTargetID() {
		return result.getTargetID();
	}

	@Override
	public RegressionMethod getRegressionMethod() {
		return result.getRegressionMethod();
	}

	@Override
	public IIndicators getItems() {
		return new IndicatorsImmutable(result.getItems());
	}
}