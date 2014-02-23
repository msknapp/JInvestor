package com.KnappTech.sr.model.beans;

import java.io.Serializable;

import com.KnappTech.sr.model.comp.Indicator;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;
import com.KnappTech.xml.SimpleNode;

public class IndicatorBean implements Serializable, NodeExpressable {
	public static final long serialVersionUID = 201102131410L;
	private String id = "";
	private double factor = 0;
	private double error = 0;
	private byte type = 0; // economic record by default.
	private boolean derivative = false;

	public IndicatorBean() {
		
	}
	
	public IndicatorBean(Indicator i) {
		this.id = i.getID();
		this.factor = i.getFactor();
		this.error = i.getError();
		this.type = i.getType();
		this.derivative = i.isDerivative();
	}

	public IndicatorBean(INode nn) {
		this.id = nn.getSubNodeValue("id");
		this.factor = Double.parseDouble(nn.getSubNodeValue("f"));
		this.error = Double.parseDouble(nn.getSubNodeValue("e"));
		this.type = Byte.parseByte(nn.getSubNodeValue("t"));
		this.derivative = nn.getSubNodeValue("d").equals("t");
	}

	@Override
	public INode toNode() {
		SimpleNode node = new SimpleNode("ib");
		node.addSubNode("id",id);
		node.addSubNode("f",factor);
		node.addSubNode("e",error);
		node.addSubNode("t",type);
		node.addSubNode("d",derivative ? "t" : "f");
		return node;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setFactor(double factor) {
		this.factor = factor;
	}

	public double getFactor() {
		return factor;
	}

	public void setError(double error) {
		this.error = error;
	}

	public double getError() {
		return error;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public void setDerivative(boolean derivative) {
		this.derivative = derivative;
	}

	public boolean isDerivative() {
		return derivative;
	}
}