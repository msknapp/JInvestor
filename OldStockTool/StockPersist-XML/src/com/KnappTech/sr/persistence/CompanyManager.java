package com.KnappTech.sr.persistence;

import java.io.File;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.sr.model.beans.CompanyBean;
import com.KnappTech.sr.model.comp.Companies;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.persist.PersistProperties;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class CompanyManager extends PersistenceManager<Company> {
	
	private static final CompanyManager INSTANCE = new CompanyManager();
	
	private CompanyManager() {
		PersistenceRegister.set(this);
	}
	
	public static final CompanyManager getInstance() {
		return INSTANCE;
	}

	@Override
	public Class<Company> getManagedClass() {
		return Company.class;
	}

	@Override
	protected String getBasePath() {
		String pth = PersistProperties.getDataDirectoryPath();
		if (!pth.endsWith(File.separator))
			pth+=File.separator;
		pth+="Companies";
		return pth;
	}

	@Override
	protected IdentifiableList<Company, String> createSetType() {
		return new Companies();
	}

	@Override
	protected NodeExpressable getBean(Company obj) {
		CompanyBean bean = new CompanyBean(obj);
		return bean;
	}

	@Override
	protected Company getObj(INode node) {
		if (node==null)
			throw new NullPointerException("Given a null node to build object from.");
		if (node.getSubNode("id")==null)
			return null; // cannot create without an id.
		CompanyBean bean = new CompanyBean(node);
		return Company.create(bean);
	}
}
