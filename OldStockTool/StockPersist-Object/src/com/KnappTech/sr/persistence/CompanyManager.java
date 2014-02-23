package com.KnappTech.sr.persistence;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;

import com.KnappTech.sr.model.beans.RegressionResultsBean;
import com.KnappTech.sr.model.comp.Companies;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.comp.Industry;
import com.KnappTech.sr.model.comp.RegressionResults;
import com.KnappTech.sr.model.comp.Sector;

public class CompanyManager extends PersistenceManager<Company> {
	private static final String FOLDERPATH = "Companies";
	private static final String INDUSTRYKEY = "in";
	private static final String SECTORKEY = "sc";
	private static final CompanyManager instance = new CompanyManager();
	
	private CompanyManager() {}

	public static Company getIfLoaded(String id) {
		return instance.iGetIfLoaded(id);
	}
	public static Companies getAllThatAreLoaded(Collection<String> ids) {
		return new Companies(instance.iGetAllThatAreLoaded(ids));
	}
	public static Company getIfStored(String id) throws InterruptedException {
		return instance.iGetIfStored(id);
	}
	public static Companies getAllThatAreStored(Collection<String> ids) throws InterruptedException {
		return new Companies(instance.iGetAllThatAreStored(ids));
	}
	public static boolean save(Company obj,boolean separateThread) throws InterruptedException {
		return instance.iSave(obj, separateThread);
	}
	public static boolean saveAll(Companies objects, boolean multiThread) throws InterruptedException {
		return instance.iSaveAll(objects, multiThread);
	}

	public static Companies getAllThatAreLoaded() {
		return new Companies(instance.iGetAllThatAreLoaded());
	}
	
	public static Companies getEverythingStored(char letter) throws InterruptedException {
		return new Companies(instance.iGetAllThatAreStored(letter));
	}
	
	public static void loadEverythingStored() throws InterruptedException {
		instance.iLoadAll(instance.iGetAllStoredIDs(), true, false);
	}
	
	public static void loadEverythingStored(char letter) throws InterruptedException {
		instance.iLoadAll(instance.iGetAllStoredIDs(letter), true, false);
	}
	
	public static Companies waitForEverythingStored() throws InterruptedException {
		return new Companies(instance.iWaitForAll(instance.iGetAllStoredIDs(), 60));
	}
	
	public static Companies waitForEverythingStored(char letter) throws InterruptedException {
		return new Companies(instance.iWaitForAll(instance.iGetAllStoredIDs(letter), 60));
	}

	public static HashSet<String> getAllStoredIDs() {
		return instance.iGetAllStoredIDs();
	}

	public static HashSet<String> getAllStoredIDs(char letter) {
		return instance.iGetAllStoredIDs(letter);
	}

	public static void remove(String id) {
		instance.iRemove(id);
	}

	public static void remove(LinkedHashSet<String> ids) {
		instance.iRemove(ids);
	}

	public static void clear() {
		instance.loadedObjects.clear();
	}
	
	@Override
	protected String getFolderPath() {
		return FOLDERPATH;
	}

	@Override
	protected String makeSaveString(Company obj) {
		String s=INDUSTRYKEY+"="+obj.getIndustry()+"\r";
		s+=SECTORKEY+"="+obj.getSector()+"\r";
		return s;
	}
	
	@Override
	protected Serializable makeSaveObject(Company obj) {
		return new RegressionResultsBean(obj.getRegressionResultsSet());
	}
	
	@Override
	protected Company createInstance(String id, String stringData,
			Object objectData) {
		try {
			Properties p = new Properties();
			p.load(new ByteArrayInputStream(stringData.getBytes()));
			String industryPropString = p.getProperty(INDUSTRYKEY);
			String sectorPropString = p.getProperty(SECTORKEY);
			short industryShortNumber = Short.parseShort(industryPropString);
			short sectorShortNumber = Short.parseShort(sectorPropString);
			Sector sector = SectorManager.getItem(sectorShortNumber);
			Industry industry = IndustryManager.getItem(industryShortNumber);
			Company c = null;
			if (industryShortNumber==-1 && sectorShortNumber==-1) {
				c = Company.createIndicator(id);
			} else {
				c = Company.create(id, sector, industry);
			}
			if (objectData!=null && c!=null) {
				RegressionResultsBean rb = (RegressionResultsBean)objectData;
				RegressionResults rrs = RegressionResults.create(rb);
				c.setRegressionResultsSet(rrs);
			}
			return c;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean hasStored(String id) {
		return instance.iIsStored(id);
	}

	@Override
	public Company load(String id) {
		try {
			return instance.iGetIfStored(id);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void save(Company t) {
		try {
			instance.iSave(t, false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Company get(String id) {
		return instance.get(id);
	}
}