package com.KnappTech.model;

public interface Lockable extends PermanentLockable {
	public void lock();
	public void unlock();
}
