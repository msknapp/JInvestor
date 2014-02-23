package com.KnappTech.sr.persistence;

import java.io.File;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.sr.model.beans.IndustryBean;
import com.KnappTech.sr.model.comp.Industries;
import com.KnappTech.sr.model.comp.Industry;
import com.KnappTech.sr.persist.PersistProperties;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class IndustryManager extends PersistenceManager<Industry> {
	
	private static final IndustryManager INSTANCE = new IndustryManager();
	
	private IndustryManager() {
		PersistenceRegister.set(this);
	}
	
	public static final IndustryManager getInstance() {
		return INSTANCE;
	}

	@Override
	public Class<Industry> getManagedClass() {
		return Industry.class;
	}

	@Override
	protected String getBasePath() {
		String pth = PersistProperties.getDataDirectoryPath();
		if (!pth.endsWith(File.separator))
			pth+=File.separator;
		pth+="Industries";
		return pth;
	}

	@Override
	protected IdentifiableList<Industry, String> createSetType() {
		return new Industries();
	}

	@Override
	protected NodeExpressable getBean(Industry obj) {
		IndustryBean bean = new IndustryBean(obj);
		return bean;
	}

	@Override
	protected Industry getObj(INode node) {
		IndustryBean bean = new IndustryBean(node);
		return Industry.getOrCreate(bean);
	}
}
