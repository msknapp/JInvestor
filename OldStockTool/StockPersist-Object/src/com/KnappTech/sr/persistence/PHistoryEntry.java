package com.KnappTech.sr.persistence;

import java.io.Serializable;

import com.KnappTech.model.LiteDate;

public class PHistoryEntry implements Serializable {
	private static final long serialVersionUID = 201011182055L;
	public LiteDate date = null;
	public int value = 0;
	public PHistoryEntry(LiteDate date,int value) {
		this.date = date;
		this.value = value;
	}
}