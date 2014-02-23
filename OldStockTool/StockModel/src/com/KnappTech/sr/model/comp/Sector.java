package com.KnappTech.sr.model.comp;

import java.util.Hashtable;

import com.KnappTech.sr.model.NamedKTObject;
import com.KnappTech.sr.model.beans.SectorBean;

public class Sector extends NamedKTObject {
	private static final long serialVersionUID = 201005182223L;
	
	private static final Hashtable<String,Sector> sectors = new Hashtable<String,Sector>();

	protected Sector(short index,String name) {
		super(index,name);
	}

	@Override
	public int compareTo(String o) {
		return getID().compareTo(o);
	}
	
	public static final Sector get(String name) {
		return sectors.get(name);
	}
	
	public static final Sector get(int index) {
		for (Sector s : sectors.values())
			if (s.getIDValue()==index)
				return s;
		return null;
	}
	
	public static final String getName(int index) {
		Sector s = get(index);
		if (s==null)
			return null;
		return s.getName();
	}
	
	public static final Sector getOrCreate(String name) {
		Sector s = sectors.get(name);
		if (s== null) {
			s = new Sector((short) sectors.size(),name);
			sectors.put(name, s);
		}
		return s;
	}
	
	public String toString() {
		return getName();
	}

	public static Sector getOrCreate(SectorBean bean) {
		Sector s = sectors.get(bean.getName());
		if (s!=null)
			return s;
		s = get(bean.getNumber());
		if (s==null) {
			s = new Sector(bean.getNumber(),bean.getName());
			sectors.put(bean.getName(),s);
		}
		return s;
	}
	
	public static Sectors getSectors() {
		Sectors s = new Sectors();
		s.addAll(sectors.values());
		return s;
	}
}