package com.KnappTech.sr.persistence;

import java.io.File;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.sr.model.Regressable.PriceHistories;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.beans.PriceHistoryBean;
import com.KnappTech.sr.persist.PersistProperties;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class PriceHistoryManager extends PersistenceManager<PriceHistory> {
	
	private static final PriceHistoryManager INSTANCE = new PriceHistoryManager();
	
	private PriceHistoryManager() {
		PersistenceRegister.set(this);
	}
	
	public static final PriceHistoryManager getInstance() {
		return INSTANCE;
	}

	@Override
	public Class<PriceHistory> getManagedClass() {
		return PriceHistory.class;
	}

	@Override
	protected String getBasePath() {
		String pth = PersistProperties.getDataDirectoryPath();
		if (!pth.endsWith(File.separator))
			pth+=File.separator;
		pth+="PriceHistories";
		return pth;
	}

	@Override
	protected IdentifiableList<PriceHistory, String> createSetType() {
		return new PriceHistories();
	}

	@Override
	protected NodeExpressable getBean(PriceHistory obj) {
		PriceHistoryBean bean = new PriceHistoryBean(obj);
		return bean;
	}

	@Override
	protected PriceHistory getObj(INode node) {
		if (node==null)
			throw new NullPointerException("Given a null node to build object from.");
		PriceHistoryBean bean = new PriceHistoryBean(node);
		return PriceHistory.create(bean);
	}
}