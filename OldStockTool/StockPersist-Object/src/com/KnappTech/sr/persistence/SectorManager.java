package com.KnappTech.sr.persistence;

import com.KnappTech.sr.model.comp.Sector;

public class SectorManager extends NamedObjectPersistenceManager<Sector> {
	private static final String FILENAME = "Sectors.txt";
	private static final SectorManager instance = new SectorManager();

	private SectorManager() {}

	public static int getIndex(String name) {
		return instance.iGetIndex(name);
	}
	
	public static Sector getItem(int index) {
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
	
	public static final void Add(Sector ind) {
		instance.add(ind);
	}
	
	@Override
	protected String getFileName() {
		return FILENAME;
	}
	
	public static SectorManager i() {
		return instance;
	}

	@Override
	protected Sector instantiate(String name) {
		return Sector.getOrCreate(name);
	}

	@Override
	public Sector load(String id) {
		loadIfNecessary();
		return Sector.get(id);
	}

	@Override
	public void save(Sector t) {
		save();
	}

	@Override
	public Sector get(String id) {
		loadIfNecessary();
		return Sector.get(id);
	}
}