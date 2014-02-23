package com.KnappTech.sr.model.beans;

import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.SimpleNode;

public class PriceHistoryStatusBean extends RecordStatusBean {
	private static final long serialVersionUID = 201106051208L;
	public static final String TYPENAME = "phs";
	private double beta = 999;

	public PriceHistoryStatusBean() {
		super();
	}
	
	public PriceHistoryStatusBean(PriceHistory ph) {
		super(ph);
		this.beta = ph.getBeta();
	}
	
	public PriceHistoryStatusBean(INode node) {
		super(node);
		if (node==null)
			throw new NullPointerException("Must define a node to build this bean.");
		if (node.getSubNode("beta")==null) {
			// need to find the right node.
			for (INode n : node.getSubNodes()) {
				if (n.getSubNode("beta")!=null) {
					node=n;
					break;
				}
			}
		}
		String s = node.getSubNodeValue("beta");
		beta=999;
		if (s!=null) {
			try {
				beta = Double.parseDouble(s);
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
	}
	
	@Override
	public INode toNode() {
		SimpleNode n = (SimpleNode) super.toNode();
		n.addSubNode("beta", getBeta());
		return n;
	}
	
	@Override
	public String getTypeName() {
		return TYPENAME;
	}
	
	public double getBeta() {
		return beta;
	}
	
	public void setBeta(double beta) {
		this.beta = beta;
	}
}