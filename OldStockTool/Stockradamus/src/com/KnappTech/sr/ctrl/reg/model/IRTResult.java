package com.KnappTech.sr.ctrl.reg.model;

import com.KnappTech.sr.model.constants.RegressionMethod;

public interface IRTResult {
	public double getVariance();
	public double getCoefficentOfDetermination();
	public double getRating();
	public String getTargetID();
	public RegressionMethod getRegressionMethod();
	public IIndicators getItems();
}