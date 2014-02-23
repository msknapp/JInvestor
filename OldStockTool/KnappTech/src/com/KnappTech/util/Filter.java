package com.KnappTech.util;

public interface Filter<TYPE> {

	public boolean include(TYPE object);
}
