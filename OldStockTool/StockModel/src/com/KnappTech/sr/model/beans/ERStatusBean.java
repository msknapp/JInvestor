package com.KnappTech.sr.model.beans;

import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.xml.INode;

public class ERStatusBean extends RecordStatusBean {
	public static final String TYPENAME = "ers";

	public ERStatusBean() {
		super();
	}
	
	public ERStatusBean(EconomicRecord er) {
		super(er);
	}
	
	public ERStatusBean(INode node) {
		super(node);
	}
	
	@Override
	public INode toNode() {
		return super.toNode();
	}
	
    public String getTypeName() {
		return TYPENAME;
	}
}