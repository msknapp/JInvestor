package com.KnappTech.sr.ctrl.reg.model;

import com.KnappTech.sr.model.constants.RegressionMethod;

public class RTResult implements IRTResult {
	private double variance=0;
	private double coefficentOfDetermination = 0;
	private double rating = 0;
	private String targetID = null;
	private RegressionMethod regressionMethod = null;
	private Indicators items = null;
	
	public RTResult() {
		
	}
	
	public void setVariance(double variance) {
		this.variance = variance;
	}

	public void setCoefficentOfDetermination(double coefficentOfDetermination) {
		this.coefficentOfDetermination = coefficentOfDetermination;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public void setTargetID(String targetID) {
		this.targetID = targetID;
	}

	public void setRegressionMethod(RegressionMethod regressionMethod) {
		this.regressionMethod = regressionMethod;
	}

	@Override
	public double getVariance() {
		return variance;
	}

	@Override
	public double getCoefficentOfDetermination() {
		return coefficentOfDetermination;
	}

	@Override
	public double getRating() {
		return rating;
	}

	@Override
	public String getTargetID() {
		return targetID;
	}

	@Override
	public RegressionMethod getRegressionMethod() {
		return regressionMethod;
	}

	public void setItems(Indicators items) {
		this.items = items;
	}

	public Indicators getItems() {
		return items;
	}
}