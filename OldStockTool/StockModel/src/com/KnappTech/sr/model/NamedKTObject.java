package com.KnappTech.sr.model;

import com.KnappTech.model.Identifiable;

public abstract class NamedKTObject implements Identifiable<String> {
	private static final long serialVersionUID = 201005231910L;
	private final short id;
	private final String name;
	
	protected NamedKTObject(short id, String name) {
		if (name==null)
			throw new NullPointerException("Must specify name");
		if (name.length()<1) 
			throw new IllegalArgumentException("Must specify name");
		if (id<0)
			throw new IllegalArgumentException("id must be positive or zero");
		this.id=id;
		this.name = name;
	}
	
	@Override
	public final String getID() {
		return String.valueOf(id);
	}
	
	public final short getIDValue() {
		return id;
	}

	public final String getName() {
		return name;
	}
	
	@Override
	public int hashCode() {
		int hc = getName().toLowerCase().hashCode();
		if (hc<Integer.MAX_VALUE){
			hc++;
		} else {
			hc=Integer.MIN_VALUE;
		}
		return hc;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof NamedKTObject) {
			NamedKTObject nkto = (NamedKTObject)object;
			return getName().equalsIgnoreCase(nkto.getName());
		}
		return false;
	}
}