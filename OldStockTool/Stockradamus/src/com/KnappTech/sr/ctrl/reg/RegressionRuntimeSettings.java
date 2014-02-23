package com.KnappTech.sr.ctrl.reg;

import java.util.Calendar;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.Regressable.EconomicRecords;
import com.KnappTech.sr.model.constants.RegressionMethod;
import com.KnappTech.util.PermanentLock;

public class RegressionRuntimeSettings {
	private static char startChar = 'A';
	private static char endChar = 'Z';
	private static boolean runForWholeMarket=true;
	private static boolean runForLetters=true;
	private static LiteDate maxRecordStartDate = LiteDate.getOrCreate(Calendar.YEAR,-10);
	private static LiteDate minRecordEndDate = LiteDate.getOrCreate(Calendar.MONTH, -3);
	private static int minRecordPoints = 120;
	private static EconomicRecords economicRecordsToConsider = null;
	private static LiteDate minCreationDate = LiteDate.getOrCreate(Calendar.MONTH, -1);
	private static double minR2 = 0.5;
	private static boolean considerAllRecords = true;
	private static int earlyTerminationScore = 960;
	private static boolean considerDerivatives = true;
	private static int maxIndicators = 16;
	private static RegressionMethod regressionMethod = RegressionMethod.COEFFOFDET;
	private static int minEarlyTerminationScore = 920;
	private static PermanentLock lock = new PermanentLock();
	
	public static void lock() {
		lock.permanentlyLock();
	}
	
	public static char getStartChar() {
		return startChar;
	}
	public static void setStartChar(char startChar) {
		if (lock.canEdit())
		RegressionRuntimeSettings.startChar = startChar;
	}
	public static char getEndChar() {
		return endChar;
	}
	public static void setEndChar(char endChar) {
		if (lock.canEdit())
		RegressionRuntimeSettings.endChar = endChar;
	}
	public static boolean isRunForWholeMarket() {
		return runForWholeMarket;
	}
	public static void setRunForWholeMarket(boolean runForWholeMarket) {
		if (lock.canEdit())
		RegressionRuntimeSettings.runForWholeMarket = runForWholeMarket;
	}
	public static boolean isRunForLetters() {
		return runForLetters;
	}
	public static void setRunForLetters(boolean runForLetters) {
		if (lock.canEdit())
		RegressionRuntimeSettings.runForLetters = runForLetters;
	}
	public static EconomicRecords getEconomicRecordsToConsider() {
		return economicRecordsToConsider;
	}
	public static void setEconomicRecordsToConsider(EconomicRecords allEconomicRecords) {
		if (lock.canEdit())
		RegressionRuntimeSettings.economicRecordsToConsider = allEconomicRecords;
	}
	public static LiteDate getMinCreationDate() {
		return minCreationDate;
	}
	public static void setMinCreationDate(LiteDate minCreationDate) {
		if (lock.canEdit())
		RegressionRuntimeSettings.minCreationDate = minCreationDate;
	}
	public static double getMinR2() {
		return minR2;
	}
	public static void setMinR2(double minR2) {
		if (lock.canEdit())
		RegressionRuntimeSettings.minR2 = minR2;
	}
	public static boolean isConsiderAllRecords() {
		return considerAllRecords;
	}
	public static void setConsiderAllRecords(boolean considerAllRecords) {
		if (lock.canEdit())
		RegressionRuntimeSettings.considerAllRecords = considerAllRecords;
	}
	public static int getEarlyTerminationScore() {
		return earlyTerminationScore;
	}
	public static void setEarlyTerminationScore(double earlyTerminationScore) {
		if (!lock.canEdit())
			return;
		if (earlyTerminationScore<1)
			earlyTerminationScore = earlyTerminationScore*1000;
		earlyTerminationScore = (int) Math.round(earlyTerminationScore);
	}
	public static boolean isConsiderDerivatives() {
		return considerDerivatives;
	}
	public static void setConsiderDerivatives(boolean considerDerivatives) {
		if (lock.canEdit())
		RegressionRuntimeSettings.considerDerivatives = considerDerivatives;
	}
	public static LiteDate getMaxRecordstartdate() {
		return getMaxRecordStartDate();
	}

	public static void setMaxIndicators(int maxIndicators) {
		if (!lock.canEdit())
			return;
		if (maxIndicators<1)
			throw new IllegalArgumentException("Must allow at least one indicator.");
		RegressionRuntimeSettings.maxIndicators = maxIndicators;
	}

	public static int getMaxIndicators() {
		return maxIndicators;
	}
	
	public static boolean isLocked() {
		return lock.isLocked();
	}

	public static void setRegressionMethod(RegressionMethod regressionMethod) {
		if (lock.canEdit())
			RegressionRuntimeSettings.regressionMethod = regressionMethod;
	}

	public static RegressionMethod getRegressionMethod() {
		return regressionMethod;
	}

	public static void setMaxRecordStartDate(LiteDate maxRecordStartDate) {
		if (lock.canEdit())
			RegressionRuntimeSettings.maxRecordStartDate = maxRecordStartDate;
	}

	public static LiteDate getMaxRecordStartDate() {
		return maxRecordStartDate;
	}

	public static void setMinRecordEndDate(LiteDate minRecordEndDate) {
		if (lock.canEdit())
			RegressionRuntimeSettings.minRecordEndDate = minRecordEndDate;
	}

	public static LiteDate getMinRecordEndDate() {
		return minRecordEndDate;
	}

	public static int getMinRecordPoints() {
		return minRecordPoints;
	}

	public static void setMinRecordPoints(int minRecordPoints) {
		if (lock.canEdit())
			RegressionRuntimeSettings.minRecordPoints = minRecordPoints;
	}

	public static void setMinEarlyTerminationScore(int minEarlyTerminationScore) {
		if (lock.canEdit())
			RegressionRuntimeSettings.minEarlyTerminationScore = minEarlyTerminationScore;
	}

	public static int getMinEarlyTerminationScore() {
		return minEarlyTerminationScore;
	}
}