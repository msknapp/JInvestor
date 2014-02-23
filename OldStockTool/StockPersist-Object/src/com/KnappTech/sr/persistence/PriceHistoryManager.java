package com.KnappTech.sr.persistence;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.Regressable.PriceHistories;
import com.KnappTech.sr.model.Regressable.PriceHistory;

public class PriceHistoryManager extends PersistenceManager<PriceHistory> {
	private static final String FOLDERPATH = "PriceHistories";
	private static final String BETAKEY = "b";
	private static final String LASTSUCCESSFULUPDATEKEY = "lsu";
	private static final String LASTATTEMPTEDUPDATEKEY = "lau";
//	private static final String SOURCEAGENCYKEY = "s";
	private static final String UPDATEFREQUENCYKEY = "f";
	private static final String DATEFORMAT = "yyMMdd";
	private static final PriceHistoryManager instance = new PriceHistoryManager();
	
	private PriceHistoryManager() {}
	
	public static PriceHistory getIfLoaded(String id) {
		return instance.iGetIfLoaded(id);
	}
	public static PriceHistories getAllThatAreLoaded(Collection<String> ids) {
		return new PriceHistories(instance.iGetAllThatAreLoaded(ids));
	}
	public static PriceHistory getIfStored(String id) throws InterruptedException {
		return instance.iGetIfStored(id);
	}
	public static PriceHistories getAllThatAreStored(Collection<String> ids) throws InterruptedException {
		return new PriceHistories(instance.iGetAllThatAreStored(ids));
	}
	public static boolean save(PriceHistory obj,boolean separateThread) throws InterruptedException {
		return instance.iSave(obj, separateThread);
	}
	public static boolean saveAll(PriceHistories objects, boolean multiThread) throws InterruptedException {
		return instance.iSaveAll(objects, multiThread);
	}
	public static PriceHistories getEverythingStored(char letter) throws InterruptedException {
		return new PriceHistories(instance.iGetAllThatAreStored(letter));
	}
	public static PriceHistories getEverythingStored(char letter,boolean verbose) throws InterruptedException {
		return new PriceHistories(instance.iGetAllThatAreStored(letter,verbose));
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
	protected String makeSaveString(PriceHistory obj) {
		String s = "";
		s+=BETAKEY+":"+obj.getBeta()+"\r";
		s+=LASTSUCCESSFULUPDATEKEY+":"+obj.getLastSuccessfulUpdate().getFormatted(DATEFORMAT)+"\r";
		s+=LASTATTEMPTEDUPDATEKEY+":"+obj.getLastAttemptedUpdate().getFormatted(DATEFORMAT)+"\r";
//		s+=SOURCEAGENCYKEY+":"+obj.getSourceAgency()+"\r";
		s+=UPDATEFREQUENCYKEY+":"+obj.getUpdateFrequency()+"\r";
		return s;
	}

	@Override
	protected Serializable makeSaveObject(PriceHistory obj) {
		return new PHistory(obj);
	}

	public static void clear() {
		instance.loadedObjects.clear();
	}

	@Override
	protected PriceHistory createInstance(String id, String stringData,
			Object objectData) {
		PriceHistory ph = PriceHistory.create(id);
		try {
			Properties p = new Properties();
			p.load(new ByteArrayInputStream(stringData.getBytes()));
			String beta = p.getProperty(BETAKEY);
			ph.setBeta(Double.parseDouble(beta));
			String lsu = p.getProperty(LASTSUCCESSFULUPDATEKEY);
			LiteDate ld = LiteDate.getOrCreate(lsu,DATEFORMAT);
			ph.setLastSuccessfulUpdate(ld);
			String lau = p.getProperty(LASTATTEMPTEDUPDATEKEY);
			LiteDate lad = LiteDate.getOrCreate(lau,DATEFORMAT);
			ph.setLastAttemptedUpdate(lad);
//			String sa = p.getProperty(SOURCEAGENCYKEY);
//			ph.setSourceAgency(SourceAgency.parse(sa));
//			String uf = p.getProperty(UPDATEFREQUENCYKEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (objectData!=null) {
			PHistory h = (PHistory)objectData;
			for (int i = 0;i<h.hist.length;i++) {
				PHistoryEntry he = h.hist[i];
				ph.addNew(he.date, ((double)he.value)/100);
			}
		}
		return ph;
	}

	public static boolean hasStored(String id) {
		return instance.iIsStored(id);
	}

	@Override
	public PriceHistory load(String id) {
		try {
			return instance.iGetIfStored(id);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void save(PriceHistory t) {
		try {
			instance.iSave(t, false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public PriceHistory get(String id) {
		return instance.get(id);
	}
}