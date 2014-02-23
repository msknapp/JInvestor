package com.KnappTech.util;

/**
 * Makes it so you can only set an object once, after that
 * it cannot be set.
 * @author Michael Knapp
 * @param <E>
 */
public class SetOnceGuard<E> {
	private E obj = null;
	
	public final E getObject() {
		return obj;
	}
	
	public final void setObject(E obj) {
		if (canSet()) {
			this.obj = obj;
		} else {
			throw new IllegalStateException("Cannot set the object once it is already set.");
		}
	}
	
	public final boolean canSet() {
		return obj==null;
	}
	
	public final boolean isSet() {
		return obj!=null;
	}
}