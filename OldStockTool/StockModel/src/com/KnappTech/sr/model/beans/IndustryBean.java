package com.KnappTech.sr.model.beans;

import java.io.Serializable;

import com.KnappTech.sr.model.comp.Industry;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;
import com.KnappTech.xml.SimpleNode;

public class IndustryBean implements Serializable, NodeExpressable {
	private static final long serialVersionUID = 201105151349L;
	private String name = "";
	private short number = -1;
	
	public IndustryBean() {
		
	}
	
	public IndustryBean(INode node) {
		this.name = node.getSubNodeValue("nm");
		this.number = Short.parseShort(node.getSubNodeValue("no"));
	}

	@Override
	public INode toNode() {
		SimpleNode sn = new SimpleNode("I");
		sn.addSubNode(new SimpleNode("nm",name));
		sn.addSubNode(new SimpleNode("no",String.valueOf(number)));
		return sn;
	}
	
	public IndustryBean(Industry s) {
		this.name = s.getName();
		this.number = s.getIDValue();
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public short getNumber() {
		return number;
	}

	public void setNumber(short number) {
		this.number = number;
	}
}