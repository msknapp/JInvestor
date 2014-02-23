package com.KnappTech.sr.model.beans;

import java.io.Serializable;
import java.util.Collection;

import com.KnappTech.sr.model.Financial.FinancialEntry;
import com.KnappTech.sr.model.Financial.FinancialStatementAtFrequency;
import com.KnappTech.sr.model.constants.StatementType;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;
import com.KnappTech.xml.SimpleNode;

public class FSAFBean implements Serializable, NodeExpressable {
	private static final long serialVersionUID = 201102052308L;
	
	private boolean quarterly = false;
	private StatementType type = StatementType.INCOME;
	private FEBean[] entries = null;
	
	public FSAFBean() {
		
	}
	
	public FSAFBean(FinancialStatementAtFrequency fsaf) {
		this.setQuarterly(fsaf.isQuarterly());
		this.setType(fsaf.getType());
		setEntries(fsaf.getEntries());
	}
	
	public FSAFBean(INode n) {
		if (n ==null) 
			throw new NullPointerException("Given a null node.");
		quarterly = n.getName().equals("q");
		String t = n.getSubNodeValue("t");
		if (t.equals(StatementType.BALANCE.name()))
			type = StatementType.BALANCE;
		if (t.equals(StatementType.INCOME.name()))
			type = StatementType.INCOME;
		if (t.equals(StatementType.CASH.name()))
			type = StatementType.CASH;
		INode sn = n.getSubNode("en");
		if (sn==null)
			return;
		Collection<INode> ns = sn.getSubNodes();
		entries = new FEBean[ns.size()];
		int i = 0;
		for (INode enn : ns) {
			if (enn!=null && enn.getSubNodes()!=null)
				entries[i] = new FEBean(enn);
			else 
				entries[i]=null;
			i++;
		}
	}

	@Override
	public INode toNode() {
		SimpleNode node = new SimpleNode(quarterly ? "q" : "a");
		node.addSubNode("t",type.name());
		if (entries==null || entries.length<1)
			return node;
		SimpleNode en = new SimpleNode("en");
		node.addSubNode(en);
		for (FEBean f : entries) {
			if (f!=null)
				en.addSubNode(f.toNode());
		}
		return node;
	}

	public void setEntries(FEBean[] entries) {
		this.entries = entries;
	}
	
	public void setEntries(FinancialEntry[] entries) {
		this.entries = new FEBean[entries.length];
		for (int i = 0;i<entries.length;i++) {
			this.entries[i] = new FEBean(entries[i]);
		}
	}

	public FEBean[] getEntries() {
		return entries;
	}

	public void setType(StatementType type) {
		this.type = type;
	}

	public StatementType getType() {
		return type;
	}

	public void setQuarterly(boolean quarterly) {
		this.quarterly = quarterly;
	}

	public boolean isQuarterly() {
		return quarterly;
	}

	public int size() {
		if (entries==null)
			return 0;
		return entries.length;
	}
	
	public boolean hasEntries() {
		return size()>0;
	}
}