package com.KnappTech.sr.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import com.KnappTech.sr.model.Financial.FinancialHistories;
import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.beans.FHBean;

public class FinancialHistoryManager extends PersistenceManager<FinancialHistory> {
	private static final String FOLDERPATH = "FinancialHistories";
	private static final FinancialHistoryManager instance = new FinancialHistoryManager();
	
	private FinancialHistoryManager() {}

	public static FinancialHistory acqIfLoaded(String id) {
		return instance.iGetIfLoaded(id);
	}
	public static FinancialHistories getAllThatAreLoaded(Collection<String> ids) {
		return new FinancialHistories(instance.iGetAllThatAreLoaded(ids));
	}
	public static FinancialHistory getIfStored(String id) throws InterruptedException {
		return instance.iGetIfStored(id);
	}
	public static FinancialHistories getAllThatAreStored(Collection<String> ids) throws InterruptedException {
		return new FinancialHistories(instance.iGetAllThatAreStored(ids));
	}
	public static boolean save(FinancialHistory obj,boolean separateThread) throws InterruptedException {
		return instance.iSave(obj, separateThread);
	}
	public static boolean saveAll(FinancialHistories objects, boolean multiThread) throws InterruptedException {
		return instance.iSaveAll(objects, multiThread);
	}
	public static FinancialHistories getEverythingStored(char letter) throws InterruptedException {
		return new FinancialHistories(instance.iGetAllThatAreStored(letter));
	}

	public static HashSet<String> getAllStoredIDs() {
		return instance.iGetAllStoredIDs();
	}

	public static void remove(String id) {
		instance.iRemove(id);
	}

	@Override
	protected String getFolderPath() {
		return FOLDERPATH;
	}
	
	@Override
	protected String makeSaveString(FinancialHistory obj) {
		return null;
	}
	
	@Override
	protected Serializable makeSaveObject(FinancialHistory obj) {
		return new FHBean(obj);
	}

	public static void clear() {
		instance.loadedObjects.clear();
	}
	
	@Override
	protected FinancialHistory createInstance(String id, String stringData,
			Object objectData) {
		if (id==null)
			throw new NullPointerException("Must provide id to create an instance.");
		if (objectData==null)
			throw new NullPointerException("Must object data to create an instance.");
		if (id.length()<1)
			throw new IllegalArgumentException("Must provide id to create an instance.");
		FHBean fh = (FHBean)objectData;
		FinancialHistory f = FinancialHistory.create(fh);
		return f;
	}

	public static boolean hasStored(String id) {
		return instance.iIsStored(id);
	}

	@Override
	public FinancialHistory load(String id) {
		try {
			return instance.iGetIfStored(id);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void save(FinancialHistory t) {
		try {
			instance.iSave(t, false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public FinancialHistory get(String id) {
		return instance.get(id);
	}

	public static FinancialHistory getIfLoaded(String id) {
		return instance.iGetIfLoaded(id);
	}
}