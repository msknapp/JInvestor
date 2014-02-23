package com.KnappTech.model;

import java.util.Collection;
import java.util.LinkedHashSet;

public interface KTSet<E> extends KTObject {
	public boolean add(E object);
	public boolean has(String id);
	public E get(String id);
	public LinkedHashSet<String> getIDSet();
	public LinkedHashSet<E> getSubSet(LinkedHashSet<String> ids);
	public LinkedHashSet<String> missingItems(Collection<String> ids);
	public boolean containsItems(Collection<String> ids);
	public boolean isEmpty();
	public int size();
}
