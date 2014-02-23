package com.KnappTech.sr.persistence;

import java.io.File;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.sr.model.beans.ERStatusBean;
import com.KnappTech.sr.persist.PersistProperties;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class ERStatusBeanManager extends PersistenceManager<ERStatusBean> {
	
	private static final ERStatusBeanManager INSTANCE = new ERStatusBeanManager();
	
	private ERStatusBeanManager() {
		PersistenceRegister.set(this);
	}
	
	public static final ERStatusBeanManager getInstance() {
		return INSTANCE;
	}
	
	@Override
	public Class<ERStatusBean> getManagedClass() {
		return ERStatusBean.class;
	}

	@Override
	protected String getBasePath() {
		String pth = PersistProperties.getDataDirectoryPath();
		if (!pth.endsWith(File.separator))
			pth+=File.separator;
		pth+="ERStatus";
		return pth;
	}

	@Override
	protected IdentifiableList<ERStatusBean, String> createSetType() {
		return new IdentifiableList<ERStatusBean, String>() {
			
		};
	}

	@Override
	protected NodeExpressable getBean(ERStatusBean obj) {
		return obj;
	}

	@Override
	protected ERStatusBean getObj(INode node) {
		return new ERStatusBean(node);
	}

}
