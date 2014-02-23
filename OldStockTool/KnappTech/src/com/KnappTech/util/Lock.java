package com.KnappTech.util;

import java.util.ArrayList;

import com.KnappTech.model.Lockable;

public class Lock extends PermanentLock implements Lockable {
	private boolean locked = false;
	private final ArrayList<Lockable> subObjectsLockable = new ArrayList<Lockable>();
	
	public final synchronized boolean isLocked() {
		return locked;
	}
	
	public final synchronized boolean canEdit() {
		return !locked;
	}
	
	public final synchronized void addSubObject(Lockable lb) {
		subObjectsLockable.add(lb);
	}
	
	public final synchronized void lock() {
		locked = true;
		for (Lockable lb : subObjectsLockable) {
			lb.lock();
		}
	}
	
	public final synchronized void unlock() {
		if (!isPermanentlyLocked()) {
			locked = false;
			for (Lockable lb : subObjectsLockable) {
				lb.unlock();
			}
		}
	}
	
	public final synchronized void permanentlyLock() {
		super.permanentlyLock();
		lock();
	}
}