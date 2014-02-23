package com.KnappTech.sr.model.beans;

import java.io.Serializable;

import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;
import com.KnappTech.xml.SimpleNode;

public class CompanyBean implements Serializable, NodeExpressable {
	private static final long serialVersionUID = 201105151414L;
    private String id=null;

	private short sector=-1;
    private short industry=-1; 
    private RegressionResultsBean rrg = null;

	public CompanyBean() {
		
	}
	
	public CompanyBean(Company c) {
		this.id = c.getID();
		this.sector= c.getSector();
		this.industry = c.getIndustry();
		this.rrg = new RegressionResultsBean(c.getRegressionResultsSet());
	}
	
	public CompanyBean(INode n) {
		this.id = n.getSubNodeValue("id");
		this.sector = Short.valueOf(n.getSubNodeValue("s"));
		this.industry = Short.valueOf(n.getSubNodeValue("i"));
		INode sn = n.getSubNode("rrs");
		rrg = new RegressionResultsBean(sn);
	}

	@Override
	public INode toNode() {
		SimpleNode node = new SimpleNode("C");
		node.addSubNode("id",id);
		node.addSubNode(new SimpleNode("s", String.valueOf(sector)));
		node.addSubNode(new SimpleNode("i", String.valueOf(industry)));
		node.addSubNode(rrg.toNode());
		return node;
	}
	
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public short getSector() {
		return sector;
	}

	public void setSector(short sector) {
		this.sector = sector;
	}

	public short getIndustry() {
		return industry;
	}

	public void setIndustry(short industry) {
		this.industry = industry;
	}

	public RegressionResultsBean getRrg() {
		return rrg;
	}

	public void setRrg(RegressionResultsBean rrg) {
		this.rrg = rrg;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}