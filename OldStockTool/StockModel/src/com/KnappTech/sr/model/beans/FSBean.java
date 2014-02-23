package com.KnappTech.sr.model.beans;

import java.io.Serializable;
import java.util.List;

import com.KnappTech.sr.model.Financial.FinancialStatement;
import com.KnappTech.sr.model.constants.StatementType;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;
import com.KnappTech.xml.SimpleNode;

public class FSBean implements Serializable, NodeExpressable {
	private static final long serialVersionUID = 201102052241L;

	private StatementType type = null;
	private FSAFBean quarterlyStatement = null;
	private FSAFBean annualStatement = null;
	
	public FSBean() {
		
	}
	
	public FSBean(FinancialStatement fs) {
		this.setType(fs.getType());
		this.setQuarterlyStatement(new FSAFBean(fs.getQuarterlyStatement()));
		this.setAnnualStatement(new FSAFBean(fs.getAnnualStatement()));
	}
	
	public FSBean(INode n) {
		String st = n.getName();
		if (st.equals("bs"))
			type = StatementType.BALANCE;
		else if (st.equals("is"))
			type = StatementType.INCOME;
		else 
			type = StatementType.CASH;
		List<INode> nnn = n.getSubNodes("q");
		FSAFBean bn = null;
		for (INode in : nnn) {
			bn = new FSAFBean(in);
			if (quarterlyStatement==null || bn.size()>quarterlyStatement.size())
				quarterlyStatement = bn;
		}
		if (quarterlyStatement==null) {
			quarterlyStatement = new FSAFBean();
			quarterlyStatement.setType(type);
			quarterlyStatement.setQuarterly(true);
		}
		nnn = n.getSubNodes("a");
		for (INode in : nnn) {
			bn = new FSAFBean(in);
			if (annualStatement==null || bn.size()>annualStatement.size())
				annualStatement = bn;
		}
		if (annualStatement==null) {
			annualStatement = new FSAFBean();
			annualStatement.setType(type);
			annualStatement.setQuarterly(false);
		}
	}

	@Override
	public INode toNode() {
		String st = "bs";
		if (type==StatementType.INCOME)
			st = "is";
		if (type==StatementType.CASH)
			st = "cs";
		SimpleNode node = new SimpleNode(st);
		if (annualStatement.getEntries().length>0) {
			INode nnn = annualStatement.toNode();
			if (nnn.getName().equals("a"))
				node.addSubNode(nnn);
			else 
				System.out.println("  :O  an annual statement is not named 'a'");
		}
		if (quarterlyStatement.getEntries().length>0) {
			INode nnn = quarterlyStatement.toNode();
			if (nnn.getName().equals("q"))
				node.addSubNode(nnn);
			else 
				System.out.println("  :O  a quarterly statement is not named 'q'");
		}
		return node;
	}

	public void setType(StatementType type) {
		this.type = type;
	}

	public StatementType getType() {
		return type;
	}

	public void setQuarterlyStatement(FSAFBean quarterlyStatement) {
		this.quarterlyStatement = quarterlyStatement;
	}

	public FSAFBean getQuarterlyStatement() {
		return quarterlyStatement;
	}

	public void setAnnualStatement(FSAFBean annualStatement) {
		this.annualStatement = annualStatement;
	}

	public FSAFBean getAnnualStatement() {
		return annualStatement;
	}
}