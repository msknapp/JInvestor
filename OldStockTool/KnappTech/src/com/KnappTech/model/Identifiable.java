package com.KnappTech.model;

public interface Identifiable<TYPE> extends Comparable<TYPE> {
	public TYPE getID();
}
