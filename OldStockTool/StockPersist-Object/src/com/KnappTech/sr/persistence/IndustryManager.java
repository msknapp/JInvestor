package com.KnappTech.sr.persistence;

import com.KnappTech.sr.model.comp.Industry;

public class IndustryManager extends NamedObjectPersistenceManager<Industry> {
	private static final String FILENAME = "Industries.txt";
	private static final IndustryManager instance = new IndustryManager();
	
	private IndustryManager() {}

	public static int getIndex(String name) {
		return instance.iGetIndex(name);
	}
	
	public static Industry getItem(int index) {
		return instance.iGetItem(index);
	}
	
	public static String getName(int index) {
		return instance.iGetName(index);
	}
	
	public static final void Save() {
		instance.save();
	}
	
	public static final void Load() {
		instance.load();
	}
	
	public static final void Add(Industry ind) {
		instance.add(ind);
	}
	
	@Override
	protected String getFileName() {
		return FILENAME;
	}
	
	public static IndustryManager i() {
		return instance;
	}

	@Override
	protected Industry instantiate(String name) {
		return Industry.getOrCreate(name);
	}

	@Override
	public Industry load(String id) {
		loadIfNecessary();
		return Industry.get(id);
	}

	@Override
	public void save(Industry t) {
		save();
	}

	@Override
	public Industry get(String id) {
		loadIfNecessary();
		return Industry.get(id);
	}
	
}