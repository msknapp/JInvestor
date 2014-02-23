package com.KnappTech.sr.model.beans;

import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class ERBean extends RecordListBean implements NodeExpressable {
	private static final long serialVersionUID = 201105151357L;
	
	public ERBean(EconomicRecord er) {
		super(er);
	}
	
	public ERBean(INode node) {
		super(node);
	}
	
	@Override
    protected String getTypeName() {
		return "ER";
	}
	
	protected RecordStatusBean makeStatus(INode node) {
		return new ERStatusBean(node);
	}
	
	protected String getStatusTypeName() {
		return ERStatusBean.TYPENAME;
	}
}