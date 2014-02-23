package com.KnappTech.sr.model.beans;

import java.io.Serializable;
import java.util.Collection;

import com.KnappTech.sr.model.Financial.FinancialEntry;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;
import com.KnappTech.xml.SimpleNode;

public class FEBean implements Serializable, NodeExpressable {
	private static final long serialVersionUID = 201102052249L;
	private short index;
	private Double[] values = null;
	
	public FEBean() {
		
	}
	
	public FEBean(FinancialEntry fe) {
		this.setIndex(fe.getIndex());
		setValues(fe.getValues());
	}
	
	public FEBean(INode node) {
		if (node ==null) 
			throw new NullPointerException("Given a null node.");
		String ind = node.getSubNodeValue("i");
		if (ind==null || ind.length()<0)
			throw new IllegalArgumentException("Node does not specify an index.");
		index = Short.parseShort(ind);
		if (index<0)
			throw new IllegalArgumentException("Node does not specify a valid index.");
		INode n = node.getSubNode("vs");
		Collection<INode> ns = n.getSubNodes();
		values = new Double[ns.size()];
		int i=0;
		for (INode sn : ns) {
			values[i++] = Double.parseDouble(sn.getValue());
		}
	}

	@Override
	public INode toNode() {
		SimpleNode node = new SimpleNode("e");
		node.addSubNode("i", index);
		SimpleNode vs = new SimpleNode("vs");
		for (Double d : values) {
			vs.addSubNode("v",d);
		}
		node.addSubNode(vs);
		return node;
	}
	
	public void setIndex(short index) {
		this.index = index;
	}

	public short getIndex() {
		return index;
	}

	public void setValues(Double[] values) {
		this.values = values;
	}

	public Double[] getValues() {
		return values;
	}

}