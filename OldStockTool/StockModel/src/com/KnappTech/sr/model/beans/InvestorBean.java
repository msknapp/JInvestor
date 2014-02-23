package com.KnappTech.sr.model.beans;

import java.io.Serializable;
import java.util.HashSet;

import com.KnappTech.sr.model.user.Investor;
import com.KnappTech.sr.model.user.ScoringMethod;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class InvestorBean implements Serializable, NodeExpressable {
	private static final long serialVersionUID = 201105151408L;

	private String id;
	private HashSet<PortfolioBean> portfolios = new HashSet<PortfolioBean>();
	private double freeMoney = 0;
	private double maxInvestedPercentInSingleStock = 0.15;
	private double personalMarginRequirement = 0.6;
	private double personalShortMarginRequirement = 0.6;
	private double costOfMargin=0.07;
	private double tradingFee = 4.5;
	private ScoringMethod scoringMethod = new ScoringMethod();
	
	public InvestorBean() {
		
	}
	
	public InvestorBean(Investor investor) {
		
	}
	
	public InvestorBean(INode node) {
		
	}

	@Override
	public INode toNode() {
		// TODO Auto-generated method stub
		return null;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public HashSet<PortfolioBean> getPortfolios() {
		return portfolios;
	}

	public void setPortfolios(HashSet<PortfolioBean> portfolios) {
		this.portfolios = portfolios;
	}

	public double getMaxInvestedPercentInSingleStock() {
		return maxInvestedPercentInSingleStock;
	}

	public void setMaxInvestedPercentInSingleStock(
			double maxInvestedPercentInSingleStock) {
		this.maxInvestedPercentInSingleStock = maxInvestedPercentInSingleStock;
	}

	public double getPersonalMarginRequirement() {
		return personalMarginRequirement;
	}

	public void setPersonalMarginRequirement(double personalMarginRequirement) {
		this.personalMarginRequirement = personalMarginRequirement;
	}

	public double getPersonalShortMarginRequirement() {
		return personalShortMarginRequirement;
	}

	public void setPersonalShortMarginRequirement(
			double personalShortMarginRequirement) {
		this.personalShortMarginRequirement = personalShortMarginRequirement;
	}

	public double getCostOfMargin() {
		return costOfMargin;
	}

	public void setCostOfMargin(double costOfMargin) {
		this.costOfMargin = costOfMargin;
	}

	public double getTradingFee() {
		return tradingFee;
	}

	public void setTradingFee(double tradingFee) {
		this.tradingFee = tradingFee;
	}

	public ScoringMethod getScoringMethod() {
		return scoringMethod;
	}

	public void setScoringMethod(ScoringMethod scoringMethod) {
		this.scoringMethod = scoringMethod;
	}

	public void setFreeMoney(double freeMoney) {
		this.freeMoney = freeMoney;
	}

	public double getFreeMoney() {
		return freeMoney;
	}

}