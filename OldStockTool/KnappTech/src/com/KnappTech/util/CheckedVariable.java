package com.KnappTech.util;

public class CheckedVariable {
	private boolean valid = false;
	private double value = -Double.MAX_VALUE;
	private String defaultString = "n/a";
	private Domain domain = null;

	public CheckedVariable(Domain domain) {
		setDomain(domain);
	}

	public CheckedVariable(Domain domain,Double value) {
		setDomain(domain);
		setValue(value);
	}

	public boolean isValid() {
		return valid;
	}

	public void setValue(double value) {
		this.value = value;
		checkValue();
	}

	public double getValue() {
		if (valid) {
			return value;
		} else {
			System.err.println("Asked for a variable value but it is invalid.");
			Thread.dumpStack();
			return value;
		}
	}
	
	public Double getDoubleValue() {
		if (valid) 
			return value;
		return null;
	}

	public void setDefaultString(String defaultString) {
		this.defaultString = defaultString;
	}

	public String getDefaultString() {
		return defaultString;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
		checkValue();
	}
	
	private boolean checkValue() {
		valid = domain.isInDomain(value);
		return valid;
	}

	public Domain getDomain() {
		return domain;
	}
	
	public String toString() {
		if (valid) {
			return String.valueOf(value);
		} else {
			return defaultString;
		}
	}
}
