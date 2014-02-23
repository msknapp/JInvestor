package com.KnappTech.sr.persistence;

import java.io.File;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.sr.model.Financial.FinancialEntryType;
import com.KnappTech.sr.model.Financial.FinancialEntryTypes;
import com.KnappTech.sr.model.Financial.FinancialHistories;
import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.beans.FHBean;
import com.KnappTech.sr.model.beans.FTBean;
import com.KnappTech.sr.persist.PersistProperties;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class FinancialTypeManager extends
		PersistenceManager<FinancialEntryType> {
	
	private static final FinancialTypeManager INSTANCE = new FinancialTypeManager();
	
	private FinancialTypeManager() {
		PersistenceRegister.set(this);
	}
	
	public static final FinancialTypeManager getInstance() {
		return INSTANCE;
	}

	@Override
	public Class<FinancialEntryType> getManagedClass() {
		return FinancialEntryType.class;
	}

	@Override
	protected String getBasePath() {
		String pth = PersistProperties.getDataDirectoryPath();
		if (!pth.endsWith(File.separator))
			pth+=File.separator;
		pth+="FinancialEntryTypes";
		return pth;
	}

	@Override
	protected IdentifiableList<FinancialEntryType, String> createSetType() {
		return new IdentifiableList<FinancialEntryType, String>() { };
	}

	@Override
	protected NodeExpressable getBean(FinancialEntryType obj) {
		FTBean bean = new FTBean(obj);
		return bean;
	}

	@Override
	protected FinancialEntryType getObj(INode node) {
		FTBean bean = new FTBean(node);
		return FinancialEntryType.create(bean);
	}
}
