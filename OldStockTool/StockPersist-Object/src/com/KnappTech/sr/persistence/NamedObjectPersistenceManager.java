package com.KnappTech.sr.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.KnappTech.model.IPersistenceManager;
import com.KnappTech.sr.model.NamedKTObject;
import com.KnappTech.util.FileUtil;

public abstract class NamedObjectPersistenceManager<OBJ extends NamedKTObject> 
implements IPersistenceManager<OBJ,String> 
{
	
	protected final ArrayList<OBJ> data = new ArrayList<OBJ>();
	protected boolean loading = false;
	
	protected final void save() {
		try {
			File file = new File(getFilePath());
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			} 
			String s = "";
			for (OBJ o : data) {
				s+=o.getName()+"\n";
			}
			FileUtil.writeStringToFile(file, s,true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected final void load() {
		loading=true;
		File file = new File(getFilePath());
		if (file.getParentFile().exists() && file.exists()) {
			String s = FileUtil.readStringFromFile(file);
			if (s!=null && s.length()>0) {
				String[] ss = s.split("\n");
				data.clear();
				ArrayList<String> ls = new ArrayList<String>(Arrays.asList(ss));
				for (int i = 0;i<ls.size();i++) {
					String name = ls.get(i);
					instantiate(name);
				}
			}
		} 
		loading=false;
	}
	
	protected OBJ iGetItem(int index) {
		loadIfNecessary();
		if (0<=index && index<data.size()) {
			return data.get(index);
		}
		return null;
	}
	
	protected String iGetName(int index) {
		loadIfNecessary();
		if (0<=index && index<data.size()) {
			return data.get(index).getName();
		}
		return null;
	}
	
	protected int iGetIndex(String name) {
		loadIfNecessary();
		instantiate(name);
		for (int i = 0;i<data.size();i++) {
			if (data.get(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}
	
	private String getFilePath() {
		String d = PersistProperties.getDataDirectoryPath();
		if (!d.endsWith(File.separator)) {
			d+=File.separator;
		}
		d+=getFileName();
		return d;
	}
	
	@SuppressWarnings("unchecked")
	public void add(NamedKTObject namedKTObject) {
		loadIfNecessary();
		if (!data.contains(namedKTObject)) {
			data.add((OBJ)namedKTObject);
		}
	}
	
	public OBJ getItem(String name) {
		loadIfNecessary();
		for (int i = 0;i<data.size();i++) {
			OBJ o = data.get(i);
			if (o.getName().equalsIgnoreCase(name)) {
				return o;
			}
		}
		return null;
	}
	
	public boolean has(String name) {
		return getItem(name)!=null;
	}
	
	public int size() {
		return data.size();
	}
	
	public void loadIfNecessary() {
		if (data.size()<1 && !loading) {
			load();
		}
	}
	
	protected abstract String getFileName();
	protected abstract OBJ instantiate(String name);
}