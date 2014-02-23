package com.KnappTech.sr.model.beans;

import java.io.Serializable;

import com.KnappTech.sr.model.Financial.FinancialEntryType;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;
import com.KnappTech.xml.SimpleNode;

public class FTBean implements NodeExpressable, Serializable {
	private static final long serialVersionUID = 201105151913L;
	private short id=-1;
	private String name=null;
	
	public FTBean() {
		
	}

	public FTBean(FinancialEntryType t) {
		this.id = t.getIDValue();
		this.name = t.getName();
	}
	
	public FTBean(INode node) {
		this.id = Short.parseShort(node.getSubNodeValue("id"));
		this.name = node.getSubNodeValue("nm");
	}
	
	@Override
	public INode toNode() {
		SimpleNode node = new SimpleNode("ft");
		node.addSubNode("id", id);
		node.addSubNode("nm",name);
		return node;
	}
	
	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}