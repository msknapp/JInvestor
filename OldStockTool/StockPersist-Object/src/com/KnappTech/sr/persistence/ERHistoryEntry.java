package com.KnappTech.sr.persistence;

import java.io.Serializable;

import com.KnappTech.model.LiteDate;

public class ERHistoryEntry implements Serializable {
	private static final long serialVersionUID = 201011182055L;
	public LiteDate date = null;
	public double value = 0;
	public ERHistoryEntry() {}
	public ERHistoryEntry(LiteDate date,double value) {
		this.date = date;
		this.value = value;
	}
}
