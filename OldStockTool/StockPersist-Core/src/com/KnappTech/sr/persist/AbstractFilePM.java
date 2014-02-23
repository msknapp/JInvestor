package com.KnappTech.sr.persist;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.KnappTech.model.Identifiable;

public abstract class AbstractFilePM<E extends Identifiable<String>> 
extends AbstractPM<E> implements IPersistenceManager<E> {
	
	@Override
	public boolean hasStored(String id) {
		if (id==null)
			throw new NullPointerException("Must specify file id.");
		if (id.length()<1 || id.equals("null"))
			throw new IllegalArgumentException("Must specify file id.");
		return getFile(id)!=null || hasLoaded(id);
	}
	
	protected File getFile(String id) {
		if (id==null)
			throw new NullPointerException("Must specify file id.");
		if (id.length()<1 || id.equals("null"))
			throw new IllegalArgumentException("Must specify file id.");
		File base = new File(getBasePath());
		if (!base.exists())
			return null;
		File[] files = base.listFiles();
		String name= null;
		int i = 0;
		for (File f : files) {
			if (f==null)
				continue;
			name = f.getName();
			i = name.indexOf(".");
			if (i<0)
				i = name.length();
			name = name.substring(0,i);
			if (name.equals(id))
				return f;
		}
		return null;
	}

	@Override
	public Set<String> getAllStoredIDs() {
		File base = new File(getBasePath());
		if (!base.exists())
			return new HashSet<String>();
		File[] files = base.listFiles();
		Set<String> ids = new HashSet<String>(files.length);
		String name = null;
		int i = 0;
		for (File file : files) {
			if (file==null)
				continue;
			name = file.getName();
			i = name.indexOf(".");
			if (i<0) 
				i = name.length();
			name = name.substring(0,i);
			ids.add(name);
		}
		return ids;
	}

	@Override
	public Set<String> getAllStoredIDs(char letter) {
		letter = Character.toLowerCase(letter);
		File base = new File(getBasePath());
		if (!base.exists())
			return new HashSet<String>();
		File[] files = base.listFiles();
		Set<String> ids = new HashSet<String>(files.length);
		String name = null;
		int i = 0;
		for (File file : files) {
			if (file==null)
				continue;
			name = file.getName();
			if (Character.toLowerCase(name.charAt(0))!=letter)
				continue;
			i = name.indexOf(".");
			if (i<0) 
				i = name.length();
			name = name.substring(0,i);
			ids.add(name);
		}
		return ids;
	}

	protected abstract String getBasePath();
}