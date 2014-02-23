package com.KnappTech.sr.persistence;

import java.io.File;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.sr.model.beans.SectorBean;
import com.KnappTech.sr.model.comp.Sector;
import com.KnappTech.sr.model.comp.Sectors;
import com.KnappTech.sr.persist.PersistProperties;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class SectorManager extends PersistenceManager<Sector> {
	
	private static final SectorManager INSTANCE = new SectorManager();
	
	private SectorManager() {
		PersistenceRegister.set(this);
	}
	
	public static final SectorManager getInstance() {
		return INSTANCE;
	}

	@Override
	public Class<Sector> getManagedClass() {
		return Sector.class;
	}

	@Override
	protected String getBasePath() {
		String pth = PersistProperties.getDataDirectoryPath();
		if (!pth.endsWith(File.separator))
			pth+=File.separator;
		pth+="Sectors";
		return pth;
	}

	@Override
	protected IdentifiableList<Sector, String> createSetType() {
		return new Sectors();
	}

	@Override
	protected NodeExpressable getBean(Sector obj) {
		SectorBean bean = new SectorBean(obj);
		return bean;
	}

	@Override
	protected Sector getObj(INode node) {
		SectorBean bean = new SectorBean(node);
		return Sector.getOrCreate(bean);
	}
}