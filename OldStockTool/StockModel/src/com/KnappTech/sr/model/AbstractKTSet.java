package com.KnappTech.sr.model;

import java.io.Serializable;
import java.util.Collection;

import com.KnappTech.model.KTObject;
import com.KnappTech.model.KTSet;
import com.KnappTech.model.Lockable;

public abstract class AbstractKTSet<E extends KTObject> 
extends AbstractSKTSet<E>
implements KTSet<E>, Lockable,Serializable {
	private static final long serialVersionUID = 201003131133L;

	public AbstractKTSet() {
		super();
	}
	
	public AbstractKTSet(Collection<E> items) {
		super(items);
	}
	
	protected AbstractKTSet(AbstractKTSet<E> otherList) {
		super(otherList);
	}
	
	public AbstractKTSet(String id) {
		super(id);
	}
	
	public AbstractKTSet(Collection<E> items,String id) {
		super(items,id);
	}
	
	protected AbstractKTSet(AbstractKTSet<E> otherList,String id) {
		super(otherList,id);
	}
	
	protected final synchronized void lockSubObjects(){
		for (E o : this.items) {
			o.lock();
		}
	}
	
	protected final synchronized void unlockSubObjects(){
		for (E o : this.items) {
			o.unlock();
		}
	}
	
	protected final synchronized void permanentlyLockSubObjects() {
		for (E o : this.items) {
			o.permanentlyLock();
		}
	}
}