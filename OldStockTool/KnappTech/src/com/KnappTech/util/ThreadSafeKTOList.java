package com.KnappTech.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.KnappTech.model.KTObject;

public class ThreadSafeKTOList<E extends KTObject> {
	private final ArrayList<E> data = new ArrayList<E>();
	
	public synchronized final E get(int index) {
		return data.get(index);
	}
	
	public synchronized final void sortAlphabetically() {
		Collections.sort(data,new AlphabeticComparator());
	}
	
	public synchronized final boolean add(E o) {
		return data.add(o);
	}
	
	public synchronized final int getIndex(String id) {
		return Collections.binarySearch(data, id);
	}
	
	public synchronized ArrayList<E> getList() {
		return new ArrayList<E>(data);
	}
	
	public synchronized void remove(String id) {
		E obj = null;
		for (E o : data) {
			if (o.getID().equals(id)) {
				obj=o;
				break;
			}
		}
		if (obj!=null) {
			data.remove(obj);
		}
	}

	public synchronized void remove(Set<String> ids) {
		List<E> objects = new ArrayList<E>();
		for (E o : data) {
			if (ids.contains(o.getID())) {
				objects.add(o);
			}
		}
		data.removeAll(objects);
	}
	
	public synchronized void clear() {
		data.clear();
	}
	
	private class AlphabeticComparator implements Comparator<KTObject> {
		@Override
		public int compare(KTObject o1, KTObject o2) {
			if (o1!=null && o2!=null) {
				String s1 = o1.getID();
				String s2 = o2.getID();
				if (s1!=null && s2!=null) {
					return s1.compareTo(s2);
				} else if (s1==null && s2!=null) {
					return 1;
				} else if (s1!=null && s2==null) {
					return -1;
				} else {
					return 0;
				}
			} else if (o1==null && o2!=null) {
				return 1;
			} else if (o1!=null && o2==null) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}