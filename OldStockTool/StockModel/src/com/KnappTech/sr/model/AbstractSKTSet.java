package com.KnappTech.sr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.model.KTObject;
import com.KnappTech.model.KTSet;
import com.KnappTech.model.SemiKTO;

public abstract class AbstractSKTSet<E extends SemiKTO> 
extends IdentifiableList<E,String>
implements KTSet<E>, Serializable {
	private static final long serialVersionUID = 201101301028L;
	protected final String id;

	public AbstractSKTSet() {
		this.id = null;
	}
	
	public AbstractSKTSet(Collection<E> items) {
		super(items);
		this.id = null;
	}
	
	protected AbstractSKTSet(AbstractSKTSet<E> otherList) {
		super(otherList);
		this.id = null;
	}
	public AbstractSKTSet(String id) {
		if (id==null || id.length()<1) 
			id=null;
		this.id = id;
	}
	
	public AbstractSKTSet(Collection<E> items,String id) {
		super(items);
		if (id==null || id.length()<1) 
			id=null;
		this.id = id;
	}
	
	protected AbstractSKTSet(AbstractSKTSet<E> otherList,String id) {
		super(otherList);
		if (id==null || id.length()<1) 
			id=null;
		this.id = id;
	}
	
//	@Override
//	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
//		row.put("size",ReportColumnFormat.INTFORMAT,items.size());
//		for (E a : items) {
//			if (instructions.include(a))
//				a.updateReportRow(instructions,row);
//		}
//	}
	
	protected void defaultExceptionHandler(Exception e) {
		System.err.format("An exception was thrown by thread %s, in class %s, method %s%n" +
				"   type: %s, message: %s%n" +
				"   ", Thread.currentThread().getName(),getClass(),
				Thread.currentThread().getStackTrace()[2].getMethodName(),
				e.getClass(),e.getMessage());
	}

	public synchronized String getID() {
		if (id!=null)
			return id;
		if (items.size()>0) 
			return items.get(0).getID();
		return "empty-"+getClass().getName();
	}
	
	@Override 
	public String toString() {
		String str = "";
		for (SemiKTO object : items) {
			str+=object.getID()+", ";
		}
		if (str.length()>100) str = str.substring(0,100);
		if (str.isEmpty()) str = "I am empty.";
		return str;
	}
	
	public synchronized LinkedHashSet<String> missingIDs(Collection<String> ids) {
		return missingItems(ids);
	}
	
	public synchronized Collection<E> getInvalidObjects() {
		ArrayList<E> lis = new ArrayList<E>();
		for (E obj : items) {
			if (!obj.isValid()) {
				lis.add(obj);
			}
		}
		return lis;
	}
	
	public synchronized int countInvalidObjects() {
		return getInvalidObjects().size();
	}
	
	public synchronized boolean hasInvalidObject() {
		for (E obj : items) {
			if (!obj.isValid()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(Object o) {
		if (o instanceof KTObject) {
			KTObject ko = (KTObject)o;
			String s = getID();
			String so = ko.getID();
			if (s!=null && so!=null && !s.equals("") && !so.equals("")) {
				return s.equals(so);
			}
		}
		return false;
	}
	
	public int hashCode() {
		String s = getID();
		if (s!=null && !s.equals("")) {
			return s.hashCode()-589;
		}
		return -63281570;
	}
	
	@Override
	public boolean isValid() { return true; }
//	public abstract boolean save(boolean multiThread) throws InterruptedException;
}