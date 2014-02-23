package com.KnappTech.sr.persistence;

import java.io.File;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.EconomicRecords;
import com.KnappTech.sr.model.beans.ERBean;
import com.KnappTech.sr.persist.PersistProperties;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class EconomicRecordManager extends PersistenceManager<EconomicRecord> {
	
	private static final EconomicRecordManager INSTANCE = new EconomicRecordManager();
	
	private EconomicRecordManager() {
		PersistenceRegister.set(this);
	}
	
	public static final EconomicRecordManager getInstance() {
		return INSTANCE;
	}

	@Override
	public Class<EconomicRecord> getManagedClass() {
		return EconomicRecord.class;
	}

	@Override
	protected String getBasePath() {
		String pth = PersistProperties.getDataDirectoryPath();
		if (!pth.endsWith(File.separator))
			pth+=File.separator;
		pth+="EconomicRecords";
		return pth;
	}

	@Override
	protected IdentifiableList<EconomicRecord, String> createSetType() {
		return new EconomicRecords();
	}

	@Override
	protected NodeExpressable getBean(EconomicRecord obj) {
		ERBean bean = new ERBean(obj);
		return bean;
	}

	@Override
	protected EconomicRecord getObj(INode node) {
		if (node==null)
			throw new NullPointerException("Given a null node to build object from.");
		try {
			ERBean bean = new ERBean(node);
			return EconomicRecord.create(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}