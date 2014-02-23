package com.KnappTech.sr.model.comp;

import java.util.Collection;

import com.KnappTech.sr.model.NamedKTObjectSet;

public class Industries extends NamedKTObjectSet<Industry> {
	private static final long serialVersionUID = 201001230907L;
    
	public Industries() {
		
	}
	
	public Industries(Collection<Industry> objects) {
		super(objects);
	}

	@Override
	public String getItemNameForID(short id) {
		return Industry.getName(id);
	}
}