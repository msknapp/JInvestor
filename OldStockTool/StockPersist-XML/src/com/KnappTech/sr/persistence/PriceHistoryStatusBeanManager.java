package com.KnappTech.sr.persistence;

import java.io.File;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.sr.model.beans.PriceHistoryStatusBean;
import com.KnappTech.sr.persist.PersistProperties;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class PriceHistoryStatusBeanManager extends PersistenceManager<PriceHistoryStatusBean> {
	
	private static final PriceHistoryStatusBeanManager INSTANCE = new PriceHistoryStatusBeanManager();
	
	private PriceHistoryStatusBeanManager() {
		PersistenceRegister.set(this);
	}
	
	public static final PriceHistoryStatusBeanManager getInstance() {
		return INSTANCE;
	}
	
	@Override
	public Class<PriceHistoryStatusBean> getManagedClass() {
		return PriceHistoryStatusBean.class;
	}

	@Override
	protected String getBasePath() {
		String pth = PersistProperties.getDataDirectoryPath();
		if (!pth.endsWith(File.separator))
			pth+=File.separator;
		pth+="PHStatus";
		return pth;
	}

	@Override
	protected IdentifiableList<PriceHistoryStatusBean, String> createSetType() {
		return new IdentifiableList<PriceHistoryStatusBean, String>() {
			
		};
	}

	@Override
	protected NodeExpressable getBean(PriceHistoryStatusBean obj) {
		return obj;
	}

	@Override
	protected PriceHistoryStatusBean getObj(INode node) {
		return new PriceHistoryStatusBean(node);
	}
}