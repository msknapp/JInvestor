package com.KnappTech.sr.model.comp;

import java.util.Hashtable;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.sr.model.NamedKTObject;
import com.KnappTech.sr.model.beans.IndustryBean;

public class Industry extends NamedKTObject {
	private static final long serialVersionUID = 201005182222L;
	
	private static final Hashtable<String,Industry> industries = new Hashtable<String,Industry>();
	
	private Industry(short index,String name) {
		super(index,name);
	}

	@Override
	public int compareTo(String o) {
		return getID().compareTo(o);
	}
	
	public static final Industry get(String name) {
		return industries.get(name);
	}
	
	public static final Industry get(int index) {
		for (Industry ind : industries.values())
			if (ind.getIDValue()==index)
				return ind;
		return null;
	}
	
	public static final String getName(int index) {
		Industry i = get(index);
		if (i==null)
			return null;
		return i.getName();
	}
	
	public static final Industry getOrCreate(String name) {
		Industry ind = industries.get(name);
		if (ind==null) {
			ind = new Industry((short) industries.size(),name);
			industries.put(name, ind);
		}
		return ind;
	}
	
	public String toString() {
		return getName();
	}

	public static Industry getOrCreate(IndustryBean bean) {
		Industry ind = industries.get(bean.getName());
		if (ind!=null)
			return ind;
		ind = get(bean.getNumber());
		if (ind==null) {
			ind = new Industry(bean.getNumber(),bean.getName());
			industries.put(bean.getName(), ind);
		}
		return ind;
	}

	public static IdentifiableList<Industry, String> getIndustries() {
		Industries in = new Industries();
		in.addAll(industries.values());
		return in;
	}
}