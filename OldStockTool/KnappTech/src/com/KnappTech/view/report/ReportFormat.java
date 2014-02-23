package com.KnappTech.view.report;

import java.text.NumberFormat;
import java.util.Locale;

public enum ReportFormat {
	USCURRENCY (getUSCurrencyFormat()),
	PERCENT (getPercentFormat()),
	SIMPLE (getSimpleFormat()),
	THREEDECIMAL (getThreeDecimalFormat()),
	SIXDECIMAL (getSixDecimalFormat()),
	NINEDECIMAL (getNineDecimalFormat()),
	TWELVEDECIMAL (getTwelveDecimalFormat()),
	INTFORMAT (getIntegerFormat());
	
	private static String delim = "<";
	
	private NumberFormat nf = null;
	
	private ReportFormat(NumberFormat nf) {
		this.nf = nf;
	}
	
	public String format(double dbl) {
		String s = nf.format(dbl);
		s=s.replace(delim, "");
		return s;
	}
	
	private static NumberFormat getUSCurrencyFormat() {
		NumberFormat usCurrencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
		usCurrencyFormat.setMinimumFractionDigits(2);
		usCurrencyFormat.setMaximumFractionDigits(2);
		usCurrencyFormat.setMinimumIntegerDigits(1);
		usCurrencyFormat.setParseIntegerOnly(false);
		usCurrencyFormat.setGroupingUsed(false);
		return usCurrencyFormat;
	}
	
	private static NumberFormat getPercentFormat() {
		NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.US);
		percentFormat.setMinimumFractionDigits(1);
		percentFormat.setMaximumFractionDigits(1);
		percentFormat.setMinimumIntegerDigits(1);
		percentFormat.setParseIntegerOnly(false);
		return percentFormat;
	}
	
	private static NumberFormat getSimpleFormat() {
		NumberFormat simpleFormat = NumberFormat.getInstance();
		simpleFormat.setGroupingUsed(false);
		return simpleFormat;
	}
	
	private static NumberFormat getIntegerFormat() {
		NumberFormat intFormat = NumberFormat.getIntegerInstance();
		intFormat.setMaximumFractionDigits(0);
		intFormat.setGroupingUsed(false);
		return intFormat;
	}
	
	private static NumberFormat getThreeDecimalFormat() {
		NumberFormat tdf = NumberFormat.getInstance();
		tdf.setMinimumFractionDigits(0);
		tdf.setMaximumFractionDigits(3);
		tdf.setMaximumIntegerDigits(12);
		tdf.setMinimumIntegerDigits(1);
		tdf.setGroupingUsed(false);
		return tdf;
	}
	
	public static void setDelimiter(char delimiter) {
		delim=String.valueOf(delimiter);
	}
	
	public static NumberFormat getSixDecimalFormat() {
		NumberFormat tdf = NumberFormat.getInstance();
		tdf.setMinimumFractionDigits(0);
		tdf.setMaximumFractionDigits(6);
		tdf.setMaximumIntegerDigits(12);
		tdf.setMinimumIntegerDigits(1);
		tdf.setGroupingUsed(false);
		return tdf;
	}
	
	public static NumberFormat getNineDecimalFormat() {
		NumberFormat tdf = NumberFormat.getInstance();
		tdf.setMinimumFractionDigits(0);
		tdf.setMaximumFractionDigits(9);
		tdf.setMaximumIntegerDigits(12);
		tdf.setMinimumIntegerDigits(1);
		tdf.setGroupingUsed(false);
		return tdf;
	}
	
	public static NumberFormat getTwelveDecimalFormat() {
		NumberFormat tdf = NumberFormat.getInstance();
		tdf.setMinimumFractionDigits(0);
		tdf.setMaximumFractionDigits(12);
		tdf.setMaximumIntegerDigits(12);
		tdf.setMinimumIntegerDigits(1);
		tdf.setGroupingUsed(false);
		return tdf;
	}
}
