package com.KnappTech.sr.ctrl.reg;

import com.KnappTech.sr.ctrl.reg.model.IRTResult;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.comp.RegressionResult;
import com.KnappTech.util.MultipleDataPoints;

public class RegressionResultFactory {
	
	public static final RegressionResult produceRegressionResult(PriceHistory history) {
		double bestRating = -1;
		int timesAtScore = 0;
		int consecutiveImpossibleUndos = 0;
		RegressionPointsCombo rpc = new RegressionPointsCombo(history);
		IRTResult bestResult = null;
		while (rpc.shouldContinue() && bestRating<RegressionRuntimeSettings.getEarlyTerminationScore()) {
			try {
				MultipleDataPoints mdp = rpc.getMultipleDataPoints();
				IRTResult r = RegressionTestFactory.regressionTest(mdp);
				if (r.getRating()>bestRating) {
					// improved.
					bestRating = r.getRating();
					bestResult = r;
					timesAtScore=0;
					consecutiveImpossibleUndos=0;
				}
				if (r.getRating()<=bestRating) { // worse.
					timesAtScore++;
					if (timesAtScore>100 && 
							bestRating>RegressionRuntimeSettings.getMinEarlyTerminationScore())
						break;
				}
				if (r.getRating()<bestRating) { // worse.
					if (rpc.canUndo()) {
						rpc.undo();
						consecutiveImpossibleUndos=0;
					} else 
						consecutiveImpossibleUndos++;
				}
				rpc.switchOut(r.getItems().getWorstID());
			} catch (Exception e) {
				if (rpc.canUndo()) {
					rpc.undo();
					consecutiveImpossibleUndos=0;
				} else 
					consecutiveImpossibleUndos++;
			}
			if (consecutiveImpossibleUndos>10)
				break;
		}
		if (bestResult==null)
			return null;
		RegressionResult rr = buildRegressionResult(bestResult);
		rr.updateIndicatorsValue(RegressionRuntimeSettings.getEconomicRecordsToConsider());
		double average = rr.getUpToDateEstimate();
		double x = history.getLastValue();
		System.out.println("Finished regressing: "+history.getID()+
				"\n\tCoefficient of Determination: "+rr.getCoefficientOfDetermination()+
				", Z: "+rr.getZ(x, average)+
				"\n\tLast Price: "+x+", estimate: "+average);
		return rr;
	}
	
	private static RegressionResult buildRegressionResult(IRTResult r) {
		RegressionResult rr = RegressionResult.create(r.getVariance(), 
				r.getCoefficentOfDetermination(), r.getRegressionMethod(), r.getTargetID());
		rr.addAll(r.getItems().getIndicators());
		return rr;
	}
}