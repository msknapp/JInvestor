package com.KnappTech.view.report;

import java.text.NumberFormat;
import java.util.Locale;

import com.KnappTech.model.LiteDate;

public abstract class ReportColumnFormat<TYPE> {
	public static final ReportColumnFormat<Double> USCURRENCY = 
		new CurrencyFormat();
	public static final ReportColumnFormat<String> COMMENT = 
		new CommentFormat();
	public static final ReportColumnFormat<String> ID = 
		new LimitedStringFormat(12);
	public static final ReportColumnFormat<Double> PERCENT = 
		new SimpleNumberFormat(getPercentFormat());
	public static final ReportColumnFormat<Double> THREEDECIMAL = 
		new SimpleNumberFormat(getLimitedDecimalFormat(3));
	public static final ReportColumnFormat<Double> SIXDECIMAL = 
		new SimpleNumberFormat(getLimitedDecimalFormat(6));
	public static final ReportColumnFormat<Double> NINEDECIMAL = 
		new SimpleNumberFormat(getLimitedDecimalFormat(9));
	public static final ReportColumnFormat<Double> TWELVEDECIMAL = 
		new SimpleNumberFormat(getLimitedDecimalFormat(12));
	public static final ReportColumnFormat<Integer> INTFORMAT = 
		new IntFormat();
	public static final ReportColumnFormat<LiteDate> DATEFORMAT = 
		new DateFormat();
	public static final ReportColumnFormat<Boolean> BOOLEANFORMAT = 
		new BooleanFormat();
	
	private ReportColumnFormat() {
		
	}
	
	public abstract String format(TYPE value);
	
	private static final class LimitedStringFormat extends 
	ReportColumnFormat<String> {
		private final int max;
		public LimitedStringFormat(int max) {
			if (max<0)
				throw new IllegalArgumentException("Must not " +
						"limit to a negative number.");
			this.max = max;
		}
		@Override
		public String format(String value) {
			if (value.length()>max) 
				value = value.substring(0,max);
			return value;
		}
	}
	
	private static final class IntFormat extends 
	ReportColumnFormat<Integer> {
		public IntFormat() {
			
		}

		@Override
		public String format(Integer value) {
			return String.valueOf(value);
		}
	}
	
	private static final class CommentFormat extends 
	ReportColumnFormat<String> {
		public CommentFormat() {
		}
		@Override
		public String format(String value) {
			return value;
		}
	}
	private static final class SimpleNumberFormat extends 
	ReportColumnFormat<Double> {
		private final NumberFormat nf;
		public SimpleNumberFormat(NumberFormat nf) {
			if (nf==null) 
				throw new NullPointerException("Number format cannot be null.");
			this.nf = nf;
		}

		@Override
		public String format(Double value) {
			return nf.format(value);
		}
	}
	
	
	
	private static final class CurrencyFormat extends ReportColumnFormat<Double> {
		private static NumberFormat nf = getUSCurrencyFormat();
		@Override
		public String format(Double value) {
			return nf.format(value);
		}
	}
	
	private static NumberFormat getUSCurrencyFormat() {
		NumberFormat usCurrencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
		usCurrencyFormat.setMinimumFractionDigits(2);
		usCurrencyFormat.setMaximumFractionDigits(2);
		usCurrencyFormat.setMinimumIntegerDigits(1);
		usCurrencyFormat.setMaximumIntegerDigits(13);
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
	
	private static NumberFormat getLimitedDecimalFormat(int lim) {
		NumberFormat tdf = NumberFormat.getInstance();
		tdf.setMinimumFractionDigits(0);
		tdf.setMaximumFractionDigits(lim);
		tdf.setMaximumIntegerDigits(12);
		tdf.setMinimumIntegerDigits(1);
		tdf.setGroupingUsed(false);
		return tdf;
	}
	
	private static class DateFormat
		extends ReportColumnFormat<LiteDate> {
		private String format = "yyyy/MM/dd";

		@Override
		public String format(LiteDate value) {
			return value.getFormatted(format);
		}
	}
	
	private static class BooleanFormat extends ReportColumnFormat<Boolean> {
		@Override
		public String format(Boolean value) {
			return value.toString();
		}
	}
}