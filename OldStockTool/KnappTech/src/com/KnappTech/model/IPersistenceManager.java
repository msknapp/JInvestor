package com.KnappTech.model;

public interface IPersistenceManager<TYPE extends Identifiable<E>,E> {

	/**
	 * Loads the object.
	 * @param id
	 * @return
	 */
	public TYPE load(E id);
	
	/**
	 * Saves the object
	 * @param t
	 */
	public void save(TYPE t);
	
	/**
	 * Gets the object, presumably if and only if it is already loaded.
	 * @param id
	 * @return
	 */
	public TYPE get(E id);
}
