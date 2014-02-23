package com.KnappTech.sr.model.beans;

import java.io.Serializable;
import java.util.LinkedHashSet;

import com.KnappTech.sr.model.user.Portfolio;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class PortfolioBean implements NodeExpressable, Serializable {
	private static final long serialVersionUID = 201105151406L;
	private LinkedHashSet<PositionBean> positions = new LinkedHashSet<PositionBean>();
	
	public PortfolioBean() {
		
	}

	public PortfolioBean(Portfolio p) {
		
	}

	public PortfolioBean(INode node) {
		
	}

	@Override
	public INode toNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public LinkedHashSet<PositionBean> getPositions() {
		return positions;
	}

	public void setPositions(LinkedHashSet<PositionBean> positions) {
		this.positions = positions;
	}
}