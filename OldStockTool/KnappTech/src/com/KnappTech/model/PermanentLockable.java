package com.KnappTech.model;

public interface PermanentLockable {
	public void permanentlyLock();
	public boolean canEdit();
	public boolean isLocked();
}
