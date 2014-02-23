package com.KnappTech.sr.persist;

import java.util.Collection;
import java.util.Set;

import com.KnappTech.model.Identifiable;
import com.KnappTech.model.IdentifiableList;

public interface IPersistenceManager<E extends Identifiable<String>> {
	
	public Class<E> getManagedClass();
	public void clear();
	public boolean hasStored(String id);
	public boolean hasLoaded(String id);
	public E getIfLoaded(String id);
	public E getIfStored(String id,boolean separateThread) throws InterruptedException;
	public void remove(String id);
	public void remove(Collection<String> ids);
	public Set<String> getAllStoredIDs();
	public boolean save(E obj,boolean separateThread) throws InterruptedException;
	public boolean saveAll(IdentifiableList<E,String> objects, boolean multiThread) 
		throws InterruptedException;
	public IdentifiableList<E,String> getEverythingStored(char letter,boolean separateThread) 
		throws InterruptedException;
	public IdentifiableList<E,String> getEverythingStored(boolean separateThread) throws InterruptedException;
	public IdentifiableList<E,String> waitFor(Collection<String> ids) throws InterruptedException;
	public IdentifiableList<E,String> waitForAll() throws InterruptedException;
	public IdentifiableList<E,String> getAllThatAreLoaded(Collection<String> ids);
	public IdentifiableList<E,String> getAllThatAreLoaded();
	public IdentifiableList<E,String> getAllThatAreStored(Collection<String> ids,boolean separateThread) 
		throws InterruptedException;
	public void Save(boolean separateThread) throws InterruptedException;
	public void Load(boolean separateThread) throws InterruptedException;
	public Set<String> getAllStoredIDs(char letter);
	public void loadEverythingStored() throws InterruptedException;
	public IdentifiableList<E,String> waitForEverythingStored() throws InterruptedException;
	public E waitFor(String id) throws InterruptedException;
}