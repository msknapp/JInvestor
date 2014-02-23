package com.KnappTech.sr.model.Financial;

import java.util.Hashtable;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.sr.model.NamedKTObject;
import com.KnappTech.sr.model.beans.FTBean;

public class FinancialEntryType extends NamedKTObject {
	private static final long serialVersionUID = 201011122036L;
	
	private static final Hashtable<String,FinancialEntryType> types = new Hashtable<String,FinancialEntryType>();
	
	private FinancialEntryType(short index, String s) {
		super(index,s);
	}
	
	public static final FinancialEntryType get(String name) {
		synchronized (types) {
			return types.get(name);
		}
	}
	
	public static final FinancialEntryType get(int index) {
		synchronized (types) {
			for (FinancialEntryType t : types.values()) 
				if (t.getIDValue()==index)
					return t;
		}
		return null;
	}
	
	public static final String getName(int index) {
		FinancialEntryType t = get(index);
		if (t==null) 
			return null;
		return t.getName();
	}
	
	public static final short getIndex(String name) {
		FinancialEntryType t = get(name);
		if (t==null)
			return -1;
		return t.getIDValue();
	}
	
	public static final FinancialEntryType getOrCreate(String name) {
		FinancialEntryType type = null;
		synchronized (types) {
			type = types.get(name);
			if (type==null) {
				type = new FinancialEntryType((short) types.size(), name);
				types.put(name, type);
			}
		}
		return type;
	}
	
	public static final FinancialEntryType getOrCreate(short index,String name) {
		FinancialEntryType type = null;
		synchronized (types) {
			type = get(name);
			FinancialEntryType type2 = get(index);
			if (type!=null && type==type2)
				return type;
			if (type==null && type2!=null)
				throw new RuntimeException("A type at that index already exists with a different name.");
			if (type!=null && type2==null){
				System.out.println(name+" exists at index "+type.getIDValue()+" already.");
				return type;
			}
				
			type = new FinancialEntryType(index, name);
			types.put(name, type);
		}
		return type;
	}
	
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo(String o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static FinancialEntryType create(FTBean bean) {
		return getOrCreate(bean.getId(), bean.getName());
	}
	
	public static IdentifiableList<FinancialEntryType, String> createIDL() {
		IdentifiableList<FinancialEntryType, String> idl = new 
			IdentifiableList<FinancialEntryType, String>() { };
		synchronized (types) {
			idl.addAll(types.values());
		}
		return idl;
	}
}