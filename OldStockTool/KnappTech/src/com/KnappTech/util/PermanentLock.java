package com.KnappTech.util;

import com.KnappTech.model.PermanentLockable;

public class PermanentLock implements PermanentLockable {
	private boolean permanentlyLocked = false;
	
	@Override
	public synchronized boolean canEdit() {
		return !permanentlyLocked;
	}

	@Override
	public synchronized boolean isLocked() {
		return permanentlyLocked;
	}
	
	public synchronized void permanentlyLock() {
		permanentlyLocked = true;
	}
	
	protected synchronized final boolean isPermanentlyLocked() {
		return permanentlyLocked;
	}
}