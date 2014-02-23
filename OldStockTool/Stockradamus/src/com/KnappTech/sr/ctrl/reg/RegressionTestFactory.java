package com.KnappTech.sr.ctrl.reg;

import com.KnappTech.sr.ctrl.reg.model.IRTResult;
import com.KnappTech.sr.ctrl.reg.model.Indicators;
import com.KnappTech.sr.ctrl.reg.model.RTResult;
import com.KnappTech.sr.ctrl.reg.model.RTResultImmutable;
import com.KnappTech.sr.model.comp.Indicator;
import com.KnappTech.util.MultipleDataPoints;

public final class RegressionTestFactory {
	
	public static final IRTResult regressionTest(MultipleDataPoints mdp) {
		RTResult r = new RTResult();
		r.setCoefficentOfDetermination(mdp.getCoeffOfDetermination());
		r.setRating(mdp.getCoeffOfDetermination()*1000);
		r.setTargetID(mdp.getyID());
		r.setVariance(mdp.getVariance());
		r.setRegressionMethod(RegressionRuntimeSettings.getRegressionMethod());
		Indicators inds = new Indicators();
		boolean[] ds = mdp.getDerivatives();
		String[] xids = mdp.getxID();
		double[] errors = mdp.getErrors();
		double[] factors = mdp.getFactors();
		for (int i = 0;i<xids.length;i++) {
			Indicator ind = Indicator.create(xids[i], factors[i], errors[i], (byte)0, ds[i]);
			inds.add(ind);
		}
		r.setItems(inds);
		return new RTResultImmutable(r);
	}
}