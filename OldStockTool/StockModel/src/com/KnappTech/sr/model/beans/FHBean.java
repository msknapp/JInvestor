package com.KnappTech.sr.model.beans;

import java.io.Serializable;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;
import com.KnappTech.xml.SimpleNode;

public class FHBean implements Serializable, NodeExpressable {
	private static final long serialVersionUID = 201102052305L;

	private String id = null;
	private LiteDate lastUpdate = null;
	
	private FSBean balanceStatement = null;
	private FSBean incomeStatement = null;
	private FSBean cashStatement = null;
	
	public FHBean() {
		
	}
	
	public FHBean(FinancialHistory fh) {
		this.setId(fh.getID());
		this.setLastUpdate(fh.getLastUpdate());
		this.setBalanceStatement(new FSBean(fh.getBalanceStatement()));
		this.setIncomeStatement(new FSBean(fh.getIncomeStatement()));
		this.setCashStatement(new FSBean(fh.getCashStatement()));
	}
	
	public FHBean(INode node) {
		String format = "yyyyMMdd";
		this.id = node.getSubNodeValue("id");
		if (node.getSubNode("lu")!=null)
		this.lastUpdate = LiteDate.getOrCreate(node.getSubNodeValue("lu"), format);
		if (node.getSubNode("bs")!=null)
			this.balanceStatement = new FSBean(node.getSubNode("bs"));
		if (node.getSubNode("is")!=null)
			this.incomeStatement = new FSBean(node.getSubNode("is"));
		if (node.getSubNode("cs")!=null)
			this.cashStatement = new FSBean(node.getSubNode("cs"));
	}

	@Override
	public INode toNode() {
		String format = "yyyyMMdd";
		SimpleNode node = new SimpleNode("FH");
		node.addSubNode("id",id);
		node.addSubNode("lu",lastUpdate.getFormatted(format));
		node.addSubNode(balanceStatement.toNode());
		node.addSubNode(incomeStatement.toNode());
		node.addSubNode(cashStatement.toNode());
		return node;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setLastUpdate(LiteDate lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public LiteDate getLastUpdate() {
		return lastUpdate;
	}
	public void setBalanceStatement(FSBean balanceStatement) {
		this.balanceStatement = balanceStatement;
	}
	public FSBean getBalanceStatement() {
		return balanceStatement;
	}
	public void setIncomeStatement(FSBean incomeStatement) {
		this.incomeStatement = incomeStatement;
	}
	public FSBean getIncomeStatement() {
		return incomeStatement;
	}
	public void setCashStatement(FSBean cashStatement) {
		this.cashStatement = cashStatement;
	}
	public FSBean getCashStatement() {
		return cashStatement;
	}
}