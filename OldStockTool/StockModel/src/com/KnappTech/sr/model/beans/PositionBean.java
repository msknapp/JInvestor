package com.KnappTech.sr.model.beans;

import java.io.Serializable;

import com.KnappTech.sr.model.user.Position;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;

public class PositionBean implements NodeExpressable, Serializable {
	private static final long serialVersionUID = 201105151405L;
	private String assetID;
	private double shares = 0;
	private double pricePerShare = 0;
	
	public PositionBean() {
		
	}
	public PositionBean(Position p) {
		
	}
	public PositionBean(INode n) {
		
	}

	@Override
	public INode toNode() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getAssetID() {
		return assetID;
	}
	public void setAssetID(String assetID) {
		this.assetID = assetID;
	}
	public double getShares() {
		return shares;
	}
	public void setShares(double shares) {
		this.shares = shares;
	}
	public double getPricePerShare() {
		return pricePerShare;
	}
	public void setPricePerShare(double pricePerShare) {
		this.pricePerShare = pricePerShare;
	}
}