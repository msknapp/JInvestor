package com.KnappTech.sr.model.beans;

import java.io.Serializable;

import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class PriceHistoryBean extends RecordListBean implements Serializable, NodeExpressable {
	private static final long serialVersionUID = 201105151355L;
	
	public PriceHistoryBean(PriceHistory ph) {
		super(ph);
	}

	public PriceHistoryBean(INode node) {
		super(node);
	}
	
	protected RecordStatusBean makeStatus(INode node) {
		return new PriceHistoryStatusBean(node);
	}
	
	@Override
    protected String getTypeName() {
		return "PH";
	}
	
	public double getBeta() {
		return getStatus().getBeta();
	}
	
	public void setBeta(double beta) {
		getStatus().setBeta(beta);
	}
	
	private PriceHistoryStatusBean getStatus() {
		return (PriceHistoryStatusBean)status;
	}
	
	protected String getStatusTypeName() {
		return PriceHistoryStatusBean.TYPENAME;
	}
}