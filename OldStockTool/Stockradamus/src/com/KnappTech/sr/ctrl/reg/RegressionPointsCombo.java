package com.KnappTech.sr.ctrl.reg;

import java.util.ArrayList;
import java.util.List;

import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.EconomicRecords;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.util.MultipleDataPoints;

public class RegressionPointsCombo {
	private final PriceHistory history;
	private final EconomicRecords workingRecords = new EconomicRecords();
	private EconomicRecord previouslyRemoved = null;
	private EconomicRecord previouslyAdded = null;
	private final RecordQueue queue = new RecordQueue();
	
	public RegressionPointsCombo(PriceHistory history) {
		if (history==null)
			throw new NullPointerException("Must define price history.");
		this.history = history;
		workingRecords.setMaximumSize(RegressionRuntimeSettings.getMaxIndicators());
		List<EconomicRecord> tempList = new ArrayList<EconomicRecord>(RegressionRuntimeSettings.getMaxIndicators());
		for (int i = 0;i<RegressionRuntimeSettings.getMaxIndicators();i++) {
			tempList.add(queue.next());
		}
		workingRecords.addAll(tempList);
	}

	public boolean shouldContinue() {
		if (!queue.hasNext())
			return false;
		return true;
	}

	public void switchOut(String worstID) {
		previouslyRemoved = workingRecords.get(worstID);
		workingRecords.remove(worstID);
		previouslyAdded = queue.next();
		workingRecords.add(previouslyAdded);
	}
	
	public boolean canUndo() {
		return (previouslyRemoved!=null && previouslyAdded!=null);
	}

	public void undo() {
		if (!canUndo()) {
			System.out.println("Trying to undo when operation never happened.");
			return;
		}
		workingRecords.remove(previouslyAdded.getID());
		workingRecords.add(previouslyRemoved);
	}

	public MultipleDataPoints getMultipleDataPoints() {
		return workingRecords.getMultipleDataPoints(history);
	}
}