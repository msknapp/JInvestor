package com.KnappTech.sr.persistence;

import java.io.File;
import java.util.Collection;

import com.KnappTech.model.IdentifiableList;
import com.KnappTech.sr.model.beans.InvestorBean;
import com.KnappTech.sr.model.comp.Companies;
import com.KnappTech.sr.model.user.Investor;
import com.KnappTech.sr.model.user.Investors;
import com.KnappTech.sr.model.user.Portfolio;
import com.KnappTech.sr.model.user.ScoringMethod;
import com.KnappTech.sr.persist.PersistProperties;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class InvestorManager extends PersistenceManager<Investor> {
	
	private static final InvestorManager INSTANCE = new InvestorManager();
	
	private InvestorManager() {
		PersistenceRegister.set(this);
	}
	
	public static final InvestorManager getInstance() {
		return INSTANCE;
	}

	@Override
	public Class<Investor> getManagedClass() {
		return Investor.class;
	}

	@Override
	protected String getBasePath() {
		String pth = PersistProperties.getDataDirectoryPath();
		if (!pth.endsWith(File.separator))
			pth+=File.separator;
		pth+="Investors";
		return pth;
	}

	@Override
	protected IdentifiableList<Investor, String> createSetType() {
		return new Investors();
	}

	@Override
	protected NodeExpressable getBean(Investor obj) {
		InvestorBean bean = new InvestorBean(obj);
		return bean;
	}

	@Override
	protected Investor getObj(INode node) {
		InvestorBean bean = new InvestorBean(node);
		return Investor.create(bean);
	}
	
	@Override
	public Investor getIfStored(String id, boolean separateThread)
			throws InterruptedException {
		return MichaelKnapp();
	}
	
	private Investor MichaelKnapp() {
		Collection<String> portfolio = PersistProperties.loadPortfolioTickers();
		Companies companies = null;
		try {
			companies = (Companies) CompanyManager.getInstance().getAllThatAreStored(portfolio,false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Investor michaelKnapp =Investor.create("Michael Knapp");
		michaelKnapp.setScoringMethod(new ScoringMethod.NoShortingScoringMethod());
		michaelKnapp.getScoringMethod().setWillingToShort(false);
		michaelKnapp.depositDollars(5685.11);
		michaelKnapp.setMaxInvestedPercentInSingleStock(0.15);
		Portfolio p = new Portfolio();
		
		michaelKnapp.add(p);
		michaelKnapp.permanentlyLock();
		return michaelKnapp;
	}
}
