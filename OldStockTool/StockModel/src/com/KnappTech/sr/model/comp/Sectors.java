package com.KnappTech.sr.model.comp;

import java.util.Collection;

import com.KnappTech.sr.model.NamedKTObjectSet;

public class Sectors extends NamedKTObjectSet<Sector> {
	private static final long serialVersionUID = 201001230909L;

	public Sectors() {
		
	}
	
	public Sectors(Collection<Sector> objects) {
		super(objects);
	}
	@Override
	public String getItemNameForID(short id) {
		return Sector.getName(id);
	}
}
