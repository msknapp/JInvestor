package com.KnappTech.util;

public class DataPoints {
	private double[] x = null;
	private double[] y = null;
	
	
	public DataPoints(double[] x,double[] y) {
		if (x.length!=y.length) {
			System.err.println("Created invalid data points.");
		}
		this.x = x;
		this.y = y;
	}
	
	public double[] getX() {
		return x;
	}

	public double[] getY() {
		return y;
	}
	
	public double getStandardError() {
		// TODO
		return 0;
	}
	
	public double getCorrelation(boolean population) {
		return getCovariance()/(getStandardDeviationX(population)*getStandardDeviationY(population));
	}
	
	// verified
	public double getSlope() {
		double n = x.length;
		double num = n*getProductsThenSumThem()-(getSumX()*getSumY());
		double den = n*getSquaresOfXThenSumThem()-getSumOfXThenSquareIt();
		if (den!=0) {
			return num/den;
		} else {
			return Double.MAX_VALUE;
		}
	}

	// verified
	public double getIntercept() {
		return (getAverageY()-getSlope()*getAverageX());
	}

	// verified
	public static double getVariance(double[] values,boolean population) {
		if (values.length<2)
			return 0;
		double avg = getAverage(values);
		double sum = 0;
		for (double d : values) {
			double diff = d-avg;
			double sq = Math.pow(diff, 2);
			sum+=sq;
		}
		return (sum/(values.length-(population ? 0 : 1)));
	}

	// verified
	public double getVarianceX(boolean population) {
		return getVariance(x,population);
	}
	
	// verified
	public double getVarianceY(boolean population) {
		return getVariance(y,population);
	}

	// verified
	public static double getCovariance(double[] x, double[] y) {
		return getAverageProduct(x, y)-getAverage(x)*getAverage(y);
	}

	// verified
	public double getCovariance() {
		return getCovariance(x,y);
	}

	// verified
	public static double getAverageProduct(double[] x, double[] y) {
		double sum = 0;
		for (int i = 0;i<x.length;i++) {
			sum += x[i]*y[i];
		}
		if (x.length>0) {
			return sum/x.length;
		} else {
			return 0;
		}
	}

	// verified
	public double getAverageProduct() {
		return getAverageProduct(x, y);
	}

	// verified
	public static double getAverage(double[] something) {
		double sum = getSum(something);
		if (something.length>0) {
			return sum/((double)something.length);
		} else {
			return 0;
		}
	}

	// verified
	public static double getSum(double[] something) {
		double sum = 0;
		for (double d : something) {
			sum+=d;
		}
		return sum;
	}

	// verified
	public static double getSumThenSquareIt(double[] something) {
		return Math.pow(getSum(something), 2);
	}

	// verified
	public static double getSquaresThenSumThem(double[] something) {
		double sum = 0;
		for (double d : something) {
			sum+=Math.pow(d, 2);
		}
		return sum;
	}
	
	// verified.
	public double getSumX() {
		return getSum(x);
	}

	// verified.
	public double getSumY() {
		return getSum(y);
	}
	
	// verified
	public double getSumOfXThenSquareIt() {
		return getSumThenSquareIt(x);
	}

	// verified
	public double getSumOfYThenSquareIt() {
		return getSumThenSquareIt(y);
	}

	// verified
	public double getSquaresOfXThenSumThem() {
		return getSquaresThenSumThem(x);
	}

	// verified
	public double getSquaresOfYThenSumThem() {
		return getSquaresThenSumThem(y);
	}

	// verified
	public double getProductsThenSumThem() {
		return getSumOfFactor(x, y);
	}

	// verified
	public static double getSumOfFactor(double[] x, double[] y) {
		double sum = 0;
		for (int i = 0;i<x.length;i++) {
			sum+=x[i]*y[i];
		}
		return sum;
	}

	// verified.
	public double getAverageX() {
		return getAverage(x);
	}

	// verified.
	public double getAverageY() {
		return getAverage(y);
	}
	
	// verified.
	public static double getStandardDeviation(double[] something,boolean population) {
		return Math.pow(getVariance(something,population), 0.5);
	}
	
	// verified
	public double getStandardDeviationX(boolean population) {
		return getStandardDeviation(x,population);
	}
	
	// verified
	public double getStandardDeviationY(boolean population) {
		return getStandardDeviation(y,population);
	}

	public double getVolatilityX() {
		return getVolatility(x);
	}

	public double getVolatilityY() {
		return getVolatility(y);
	}

	public double getVolatility(double[] values) {
		double sum = 0;
		double dev = 0;
		int count = 0;
		double lastValue = 0;
		double currentValue = 0;
		for (int i = 1;i<values.length;i++) {
			currentValue = values[i];
			lastValue = values[i-1];
			dev = Math.abs(currentValue-lastValue);
			sum+=dev;
			count++;
		}
		if (count>0) {
			return sum/count;
		}
		return -1;
	}
}