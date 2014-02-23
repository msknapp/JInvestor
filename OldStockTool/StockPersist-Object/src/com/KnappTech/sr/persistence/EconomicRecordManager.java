package com.KnappTech.sr.persistence;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.EconomicRecords;
import com.KnappTech.sr.model.constants.SourceAgency;

public class EconomicRecordManager extends PersistenceManager<EconomicRecord>{
	private static final String FOLDERPATH = "EconomicRecords";
	private static final String LASTSUCCESSFULUPDATEKEY = "lsu";
	private static final String LASTATTEMPTEDUPDATEKEY = "lau";
	private static final String SOURCEAGENCYKEY = "s";
	private static final String UPDATEFREQUENCYKEY = "f";
	private static final String DATEFORMAT = "yyMMdd";

	private static final EconomicRecordManager instance = new EconomicRecordManager();
	
	private EconomicRecordManager() {}
	
	public static EconomicRecord getIfLoaded(String id) {
		return instance.iGetIfLoaded(id);
	}
	public static EconomicRecords getAllThatAreLoaded(Collection<String> ids) {
		return new EconomicRecords(instance.iGetAllThatAreLoaded(ids));
	}
	public static EconomicRecords getAllThatAreLoaded() {
		return new EconomicRecords(instance.iGetAllThatAreLoaded());
	}
	public static EconomicRecord getIfStored(String id) throws InterruptedException {
		return instance.iGetIfStored(id);
	}
	public static EconomicRecords getAllThatAreStored(Collection<String> ids) throws InterruptedException {
		return new EconomicRecords(instance.iGetAllThatAreStored(ids));
	}
	public static EconomicRecords getAllThatAreStored(Collection<String> ids,boolean verbose) throws InterruptedException {
		return new EconomicRecords(instance.iGetAllThatAreStored(ids,verbose));
	}
	public static boolean save(EconomicRecord obj,boolean separateThread) throws InterruptedException {
		return instance.iSave(obj, separateThread);
	}
	public static boolean saveAll(EconomicRecords objects, boolean multiThread) throws InterruptedException {
		return instance.iSaveAll(objects, multiThread);
	}
	
	public static EconomicRecords getEverythingStored(char letter) throws InterruptedException {
		return new EconomicRecords(instance.iGetAllThatAreStored(letter));
	}

	public static HashSet<String> getAllStoredIDs() {
		return instance.iGetAllStoredIDs();
	}
	
	public static EconomicRecords getEverythingStored(boolean separateThread) 
	throws InterruptedException {
		if (!separateThread) {
			return new EconomicRecords(instance.iGetAllThatAreStored());
		} else {
			Runnable r = new Runnable() {
				public void run() {
					try {
						getEverythingStored(false);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			Thread t = new Thread(r);
			t.start();
		}
		return null;
	}

	public static void remove(String id) {
		instance.iRemove(id);
	}
	
	public static EconomicRecords waitFor(Collection<String> ids) throws InterruptedException {
		return new EconomicRecords(instance.iWaitForAll(ids,60));
	}
	
	public static EconomicRecords waitForAll() throws InterruptedException {
		return new EconomicRecords(instance.iWaitForAll(60));
	}
	
	@Override
	protected String getFolderPath() {
		return FOLDERPATH;
	}

	@Override
	protected String makeSaveString(EconomicRecord obj) {
		String s = "";
		s+=LASTSUCCESSFULUPDATEKEY+":"+obj.getLastSuccessfulUpdate().getFormatted(DATEFORMAT)+"\r";
		s+=LASTATTEMPTEDUPDATEKEY+":"+obj.getLastAttemptedUpdate().getFormatted(DATEFORMAT)+"\r";
		s+=SOURCEAGENCYKEY+":"+obj.getSourceAgency()+"\r";
		s+=UPDATEFREQUENCYKEY+":"+obj.getUpdateFrequency()+"\r";
		return s;
	}

	@Override
	protected Serializable makeSaveObject(EconomicRecord obj) {
		return new ERHistory(obj);
	}

	public static void clear() {
		instance.loadedObjects.clear();
	}

	@Override
	protected EconomicRecord createInstance(String id, String stringData,
			Object objectData) {
		try {
			Properties p = new Properties();
			p.load(new ByteArrayInputStream(stringData.getBytes()));
			String lsu = p.getProperty(LASTSUCCESSFULUPDATEKEY);
			LiteDate ld = LiteDate.getOrCreate(lsu,DATEFORMAT);
			String lau = p.getProperty(LASTATTEMPTEDUPDATEKEY);
			LiteDate lad = LiteDate.getOrCreate(lau,DATEFORMAT);
			String sa = p.getProperty(SOURCEAGENCYKEY);
			SourceAgency sourceAgency = SourceAgency.parse(sa);
			EconomicRecord er = EconomicRecord.createInstance(id, sourceAgency);
			er.setLastSuccessfulUpdate(ld);
			er.setLastAttemptedUpdate(lad);
//			String uf = p.getProperty(UPDATEFREQUENCYKEY);
			ERHistory h = (ERHistory)objectData;
			for (int i = 0;i<h.hist.length;i++) {
				ERHistoryEntry he = h.hist[i];
				er.addNew(he.date, he.value);
			}
			return er;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean hasStored(String id) {
		return instance.iIsStored(id);
	}

	@Override
	public EconomicRecord load(String id) {
		try {
			return instance.iGetIfStored(id);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void save(EconomicRecord t) {
		try {
			instance.iSave(t, false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public EconomicRecord get(String id) {
		return instance.get(id);
	}
}