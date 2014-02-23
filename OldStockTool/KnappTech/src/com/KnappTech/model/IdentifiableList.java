package com.KnappTech.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import com.KnappTech.util.Lock;

/**
 * Supports a collection of objects that follows these rules:
 * 1. Each object is identifiable by some object.
 * 2. Every id must be unique in the list.
 * 3. The list is ordered so it is fast at finding things.
 * 4. The list is synchronized, so you can multi-thread with this
 * @author Michael Knapp
 * @param <OBJECTTYPE> the type of objects in the list.
 * @param <IDTYPE> The object type that identifies each object
 */
public class IdentifiableList<OBJECTTYPE extends Identifiable<IDTYPE>,IDTYPE> 
implements Lockable,Serializable {
	public static final long serialVersionUID = 201102122313L;
	protected final ArrayList<OBJECTTYPE> items = new ArrayList<OBJECTTYPE>();
	private transient final IDComparator c = new IDComparator();
	private transient final Lock lock = new Lock();
	private int maximumSize = Integer.MAX_VALUE;
	
	public IdentifiableList() {
		
	}
	
	public IdentifiableList(Collection<OBJECTTYPE> items) {
		addAll(items);
	}
	
	protected IdentifiableList(IdentifiableList<OBJECTTYPE,IDTYPE> otherList) {
		this.maximumSize = otherList.maximumSize;
		this.items.addAll(otherList.items);
	}
	
	public final synchronized boolean add(OBJECTTYPE item) {
		if (canAdd(item)) {
			boolean b = items.add(item);
			if (b) {
//				System.out.println("-- sorting a list in add method, have you considered addall? --");
				Collections.sort(items,c);
			}
			return b;
		} else if (item==null) {
			throw new IllegalArgumentException("Tried adding null object to an id list.");
		}
		return false;
	}
	
	public final synchronized void addAll(Collection<OBJECTTYPE> items) {
		if (!lock.canEdit() || size()+items.size()>maximumSize)
			return;
		addInOrder(items);
		Collections.sort(this.items,c);
	}
	
	public final synchronized void addInOrder(Collection<OBJECTTYPE> items) {
		if (!lock.canEdit() || size()+items.size()>maximumSize)
			return;
		for (OBJECTTYPE item : items) {
			if (item!=null && item.getID()!=null) {
				if (!has(item))
					this.items.add(item);
			}
		}
	}
	
	public final synchronized boolean canAdd(OBJECTTYPE item) {
		return (item!=null && lock.canEdit() && item.getID()!=null && 
				!has(item) && size()<maximumSize);
	}
	
	public final synchronized boolean remove(IDTYPE id) {
		if (lock.canEdit()) {
			OBJECTTYPE item = get(id);
			if (item!=null) {
				return items.remove(item);
			}
		}
		return false;
	}
	
	public final synchronized OBJECTTYPE get(IDTYPE id) {
		if (id==null)
			return null;
		int index = Collections.binarySearch(items, id);
		if (index>=0) {
			OBJECTTYPE o = items.get(index);
			if (o.getID().equals(id))
				return o;
		}
		return null;
	}
	
	public final synchronized OBJECTTYPE getUnordered(IDTYPE id) {
		for (OBJECTTYPE o : this.items)
			if (o.getID().equals(id))
				return o;
		return null;
	}
	
	public final synchronized LinkedHashSet<IDTYPE> getIDSet() {
		LinkedHashSet<IDTYPE> results = new LinkedHashSet<IDTYPE>();
		for (OBJECTTYPE c : items) {
			results.add(c.getID());
		}
		return results;
	}

	public final synchronized LinkedHashSet<OBJECTTYPE> getSubSet(LinkedHashSet<IDTYPE> ids) {
		LinkedHashSet<OBJECTTYPE> results = new LinkedHashSet<OBJECTTYPE>();
		for (OBJECTTYPE c : items) {
			if (ids.contains(c.getID())){
				results.add(c);
			}
		}
		return results;
	}
	
	public final synchronized boolean has(OBJECTTYPE item) {
		return has(item.getID());
	}
	
	public final synchronized boolean has(IDTYPE id) {
		return (get(id)!=null);
	}
	
	public final synchronized LinkedHashSet<IDTYPE> missingItems(Collection<IDTYPE> ids) {
		LinkedHashSet<IDTYPE> copy = new LinkedHashSet<IDTYPE>(ids);
		for (OBJECTTYPE item : items) {
			copy.remove(item.getID());
		}
		return copy;
	}

	public final synchronized boolean containsIDs(Collection<IDTYPE> ids) {
		return (missingItems(ids).isEmpty());
	}

	public final synchronized boolean containsItems(Collection<IDTYPE> ids) {
		return (missingItems(ids).isEmpty());
	}
	
	public final synchronized boolean isEmpty() {
		return items.isEmpty();
	}
	
	public final synchronized int size() {
		return items.size();
	}
	
	private class IDComparator implements Comparator<OBJECTTYPE> {
		@Override
		public int compare(OBJECTTYPE o1, OBJECTTYPE o2) {
			return o1.compareTo(o2.getID());
		}
	}
	
	public final synchronized boolean isLocked() {
		return lock.isLocked();
	}
	
	public final synchronized boolean canEdit() {
		return lock.canEdit();
	}
	
	public final synchronized void lock() {
		lock.lock();
	}
	
	public final synchronized void unlock() {
		lock.unlock();
	}
	
	public final synchronized void permanentlyLock() {
		lock.permanentlyLock();
	}
	
	protected void addSubObjectToLock(Lockable lb) {
		lock.addSubObject(lb);
	}

	public final synchronized void setMaximumSize(int maximumSize) {
		if (size()>maximumSize) {
			throw new IllegalArgumentException("Tried setting the size to "+maximumSize+
					", but the current size is already higher: "+size());
		}
		this.maximumSize = maximumSize;
	}

	public final synchronized int getMaximumSize() {
		return maximumSize;
	}
	
	public final synchronized void clear() {
		if (lock.canEdit()) {
			items.clear();
		}
	}
	
	public final synchronized List<OBJECTTYPE> getItems() {
		return Collections.unmodifiableList(items);
	}
}