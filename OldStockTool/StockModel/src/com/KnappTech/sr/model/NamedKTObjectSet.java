package com.KnappTech.sr.model;

import java.util.Collection;
import java.util.Iterator;

import com.KnappTech.model.IdentifiableList;

public abstract class NamedKTObjectSet<E extends NamedKTObject> extends IdentifiableList<E,String> {
	private static final long serialVersionUID = 1L;

	public NamedKTObjectSet() {
		super();
	}
	
	public NamedKTObjectSet(Collection<E> objects) {
		super(objects);
	}
	
	public synchronized final String getIDOfItemWithName(String name) {
		return getItemWithName(name).getID();
	}
	
	public synchronized final short getIDValueOfItemWithName(String name) {
		return getItemWithName(name).getIDValue();
	}
	
	public synchronized final E getItemWithName(String name) {
		for (E nkto : items) {
			if (nkto.getName().equalsIgnoreCase(name)) {
				return nkto;
			}
		}
		return null;
	}

	public synchronized final E getItemWithID(String id) {
		return getItemWithID(Short.parseShort(id));
	}
	
	public synchronized final E getItemWithID(short id) {
		if (id>=0) {
			for (E s : items) {
				if (s.getIDValue()==id) {
					return s;
				}
			}
		}
		return null;
	}
	
	public synchronized final String getItemNameForID(String id) {
		return getItemWithID(id).getName();
	}
	
	public synchronized final short nextID() {
		short maxID = -1;
		for (E sin : items) {
			short val = Short.parseShort(sin.getID());
			if (val>maxID) {
				maxID=val;
			}
		}
		return (short) (maxID+1);
	}

	public synchronized final int indexof(String name) {
		Iterator<E> iter = items.iterator();
		int i = 0;
		while(iter.hasNext()){
			E s = iter.next();
			if (s.getName().equals(name)){
				return i;
			}
			i++;
		}
		System.err.println("Warning: when trying to get the index of an industry, it was not found.");
		return -1;
	}
	
//	public short createOrGetItemID(String name) {
//		return createOrGetItem(name).getIDValue();
//	}
//	
//	public E createOrGetItem(String name) {
//		if (has(name)) {
//			return getItemWithName(name);
//		} else {
//			E item = createSubObject();
//			item.setName(name);
//			return item;
//		}
//	}

	public abstract String getItemNameForID(short id);
}