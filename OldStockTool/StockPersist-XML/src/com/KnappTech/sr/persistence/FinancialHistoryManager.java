package com.KnappTech.sr.persistence;

import java.io.File;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.sr.model.Financial.FinancialHistories;
import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.beans.FHBean;
import com.KnappTech.sr.persist.PersistProperties;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class FinancialHistoryManager extends PersistenceManager<FinancialHistory> {
	
	private static final FinancialHistoryManager INSTANCE = new FinancialHistoryManager();
	
	private FinancialHistoryManager() {
		PersistenceRegister.set(this);
	}
	
	public static final FinancialHistoryManager getInstance() {
		return INSTANCE;
	}

	@Override
	public Class<FinancialHistory> getManagedClass() {
		return FinancialHistory.class;
	}

	@Override
	protected String getBasePath() {
		String pth = PersistProperties.getDataDirectoryPath();
		if (!pth.endsWith(File.separator))
			pth+=File.separator;
		pth+="FinancialHistories";
		return pth;
	}

	@Override
	protected IdentifiableList<FinancialHistory, String> createSetType() {
		return new FinancialHistories();
	}

	@Override
	protected NodeExpressable getBean(FinancialHistory obj) {
		FHBean bean = new FHBean(obj);
		return bean;
	}

	@Override
	protected FinancialHistory getObj(INode node) {
		if (node==null)
			throw new NullPointerException("Given a null node to build object from.");
		if (node.getSubNode("id")==null)
			return null; // cannot create without an id.
		try {
			FHBean bean = new FHBean(node);
			return FinancialHistory.create(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
