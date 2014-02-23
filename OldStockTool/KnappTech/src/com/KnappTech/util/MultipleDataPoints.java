package com.KnappTech.util;

import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression;

/**
 * A grouping of data points where there is one list of y points,
 * and multiple lists of x points.  The length of x (not x[]) must
 * equal the length of y.  The length of x[] is equal to the number
 * of x input variables there are.
 * @author Michael Knapp
 *
 */
public class MultipleDataPoints {
	private final double[][] x;
	private final double[] y;
	private final String yID;
	private final String[] xID;
	private final boolean[] derivatives;
	
	private OLSMultipleLinearRegression reg = new OLSMultipleLinearRegression();
	private double[] errors = null;
	private double[] factors = null;
	private double variance = -1;
	private double stdev = -1;
	
	private MultipleDataPoints(double[][] x, double[] y,
			String[] xID,String yID,boolean[] derivatives) {
		this.yID = yID;
		this.xID = xID;
		this.x = x;
		this.y = y;
		this.derivatives = derivatives;
		reg.newSampleData(y, x);
		variance = reg.estimateRegressandVariance();
		errors = reg.estimateRegressionParametersStandardErrors();
		factors = reg.estimateRegressionParameters();
		stdev = Math.pow(getVariance(),0.5);
	}
	
	public static final MultipleDataPoints create(double[][] x, double[] y, 
			String[] xID,String yID,boolean[] derivatives) {
		int xl = x.length;
		if (x!=null && y!=null && yID!=null && xID!=null) {
			if (xl>0 && x[0].length>0 && y.length>0 && xl==y.length &&
					x[0].length==xID.length && yID.length()>0 && 
					derivatives.length==xID.length) {
				boolean b = true;
				for (String xi : xID) {
					if (xi==null || xi.length()<1) {
						b=false;
						break;
					}
				}
				if (b) {
					return new MultipleDataPoints(x,y,xID,yID,derivatives);
				}
			}
		}
		throw new IllegalArgumentException("Cannot create multiple data points with these inputs.");
	}
	
	public String toString() {
		String s = "Y:";
		
		if (x.length<1 || y.length<1) {
			return "I am empty";
		}
		if (x.length>5 && y.length>5) {
			int z = x[0].length;
			for (int i = 0;i<z;i++) {
				s+="\tX"+i;
			}
			s+="\n";
			for (int i = 0;i<2;i++) {
				s+=y[i]+"\t";
				for (int j = 0;j<z;j++) {
					s+=x[i][j]+"\t";
				}
				s+="\n";
			}
			for (int i = y.length-2;i<y.length;i++) {
				s+=y[i]+"\t";
				for (int j = 0;j<z;j++) {
					s+=x[i][j]+"\t";
				}
				s+="\n";
			}
		} else {
			int z = x[0].length;
			for (int i = 0;i<z;i++) {
				s+="\tX"+i;
			}
			s+="\n";
			for (int i = 0;i<y.length;i++) {
				s+=y[i]+"\t";
				for (int j = 0;j<z;j++) {
					s+=x[i][j]+"\t";
				}
				s+="\n";
			}
		}
		return s;
	}
	
	public double getVariance() {
		if (variance<0) {
			variance = reg.estimateRegressandVariance();
		}
		return variance;
	}
	
	public double getStDev() {
		if (stdev<0) {
			stdev = Math.pow(getVariance(),0.5);
		}
		return stdev;
	}
	
	public double[] getErrors() {
		if (errors==null) {
			errors = reg.estimateRegressionParametersStandardErrors();
		}
		return errors;
	}
	
	/**
	 * Gets the error of the item at the index provided.
	 * @param index
	 * @return
	 */
	public double getError(int index) {
		return getErrors()[index];
	}
	
	public double getError(String id) {
		if (xID!=null && xID.length==x[0].length) {
			int i = getIndex(id);
			return getErrors()[i];
		} else {
			System.err.println("Tried getting error of an id, but the ids have " +
					"not been defined for these multiple data points.");
		}
		return -1;
	}
	
	public double getCoeffOfDetermination() {
		double[] res = reg.estimateResiduals();
		double sumSqRes = 0;
		for (int i = 0;i<res.length;i++) {
			sumSqRes += Math.pow(res[i], 2);
		}
		double sumSqY = 0;
		double avg = averageY();
		for (int i = 0;i<y.length;i++) {
			sumSqY += Math.pow((y[i]-avg), 2);
		}
		double r2 = 1-(sumSqRes/sumSqY);
		return r2;
	}
	
	public double averageY() {
		double sum = 0;
		for (int i = 0;i<y.length;i++) {
			sum+=y[i];
		}
		if (y.length>0) {
			return sum/y.length;
		}
		return 0;
	}

	public int lengthY() {
		return y.length;
	}

	public String getyID() {
		return yID;
	}

	public String[] getxID() {
		return xID;
	}

	public String getxID(int i) {
		if (i>=0 && i<xID.length) {
			return xID[i];
		}
		return null;
	}
	
	public String getIDofLargestError() {
		if (xID!=null && xID.length==numberOfInputs()) {
			double maxError = -1;
			String maxErrorID = null;
			for (int i = 0;i<numberOfInputs();i++) {
				double err = getError(i);
				if (err>maxError) {
					maxError = err;
					maxErrorID = getxID(i);
				}
			}
			return maxErrorID;
		} else {
			System.err.println("Tried finding id of largest error, but "+
					"the input ids have not been set.");
		}
		return null;
	}

	public int getIndex(String id) {
		if (id==null) 
			throw new NullPointerException("cannot get index of null id.");
		if (id.length()<1) 
			throw new IllegalArgumentException("Cannot get index of empty id.");
		for (int i = 0;i<xID.length;i++) {
			String xid = xID[i];
			if (xid.equals(id)) {
				return i;
			}
		}
		return -1;
	}
	
	public int size() {
		return y.length;
	}
	
	public int numberOfInputs() {
		return x[0].length;
	}

	public double[] getFactors() {
		if (factors==null) {
			factors = reg.estimateRegressionParameters();
		}
		return factors;
	}
	
	public double getFactor(String id) {
		int index = getIndex(id);
		return getFactors()[index];
	}

	public boolean getDerivativeStatus(String id) {
		int index = getIndex(id);
		return derivatives[index];
	}
	
	public double estimateLastValue() {
		double[] factors = getFactors();
		double[] lastValues = getLastValues();
		double sum = 0;
		for (int i = 0;i<numberOfInputs();i++) {
			sum+=factors[i]*lastValues[i];
		}
		return sum;
	}

	private double[] getLastValues() {
		double[] lastValues = new double[numberOfInputs()];
		for (int i = 0;i<numberOfInputs();i++) {
			lastValues[i] = x[size()-1][i];
		}
		return lastValues;
	}
	
	public double getLastValue(String id) {
		int index = getIndex(id);
		return getLastValues()[index];
	}
	
	public double[] getValuesFromEnd(int numberFromEnd) {
		double[] d = new double[numberOfInputs()];
		for (int i = 0;i<numberOfInputs();i++) {
			d[i] = x[size()-1-numberFromEnd][i];
		}
		return d;
	}
	
	public double getValueFromEnd(String id,int numberFromEnd) {
		int index = getIndex(id);
		return getValuesFromEnd(numberFromEnd)[index];
	}
	
	public String getLastValuesDebugString() {
		String s = "";
		for (String id : getxID()) {
			s+=id+": "+getLastValue(id)+",\n";
		}
		return s;
	}
	
	public String seeMath(int numberFromEnd) {
		String s = "";
		double sum = 0;
		for (int i = 0;i<numberOfInputs();i++) {
			String id = getxID(i);
			double factor = getFactor(id);
			double lastValue = getValueFromEnd(id,numberFromEnd);
			double product = factor*lastValue;
			sum+=product;
			s+=id+": last value="+lastValue+", factor="+
				factor+", product="+product+", sum="+
				sum+"\n";
		}
		return s;
	}

	public boolean[] getDerivatives() {
		return derivatives;
	}
}