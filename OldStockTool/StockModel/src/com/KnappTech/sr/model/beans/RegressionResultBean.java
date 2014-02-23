package com.KnappTech.sr.model.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.comp.Indicator;
import com.KnappTech.sr.model.comp.RegressionResult;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;
import com.KnappTech.xml.SimpleNode;

public class RegressionResultBean implements Serializable, NodeExpressable {
	public static final long serialVersionUID = 201102131353L; 
	private String id = "";
	private LiteDate creationDate = null;
    private double regressandVariance = -1;
	private double estimateValue = -1;
	private LiteDate estimateDate = null;
	private double coefficientOfDetermination = 0;
    private byte regressionMethod = 0;
	private ArrayList<IndicatorBean> indicators = 
		new ArrayList<IndicatorBean>();
	
	public RegressionResultBean() {
		
	}
	
	public RegressionResultBean(RegressionResult r) {
		creationDate = r.getCreationDate();
		regressandVariance = r.getRegressandVariance();
		estimateValue = r.getEstimateValue();
		estimateDate = r.getEstimateDate();
		coefficientOfDetermination = r.getCoefficientOfDetermination();
		regressionMethod = r.getRegressionMethod().getIndex();
		List<Indicator> l = r.getItems();
		for (Indicator i : l) {
			indicators.add(new IndicatorBean(i));
		}
	}
	public RegressionResultBean(INode n) {
		String format = "yyyyMMdd";
		this.id = n.getSubNodeValue("id");
		this.creationDate = LiteDate.getOrCreate(n.getSubNodeValue("cd"), format);
		this.regressandVariance = Double.parseDouble(n.getSubNodeValue("v"));
		this.estimateValue = Double.parseDouble(n.getSubNodeValue("ev"));
		if (n.getSubNode("d")!=null)
			this.estimateDate = LiteDate.getOrCreate(n.getSubNodeValue("d"), format);
		this.coefficientOfDetermination = Double.parseDouble(n.getSubNodeValue("r2"));
		this.regressionMethod = Byte.parseByte(n.getSubNodeValue("m"));
		INode sn = n.getSubNode("i");
		Collection<INode> sns = sn.getSubNodes();
		for (INode nn : sns) {
			indicators.add(new IndicatorBean(nn));
		}
	}
	
	@Override
	public INode toNode() {
		String format = "yyyyMMdd";
		SimpleNode node = new SimpleNode("rr");
		node.addSubNode("id", id);
		node.addSubNode("cd", creationDate.getFormatted(format));
		node.addSubNode("v", regressandVariance);
		node.addSubNode("ev",estimateValue);
		if (estimateDate!=null)
			node.addSubNode("d",estimateDate.getFormatted(format));
		node.addSubNode("r2",coefficientOfDetermination);
		node.addSubNode("m",regressionMethod);
		SimpleNode i = new SimpleNode("i");
		node.addSubNode(i);
		for (IndicatorBean in : indicators) {
			i.addSubNode(in.toNode());
		}
		return node;
	}

	public void setRegressandVariance(double regressandVariance) {
		this.regressandVariance = regressandVariance;
	}

	public double getRegressandVariance() {
		return regressandVariance;
	}

	public void setEstimateValue(double estimateValue) {
		this.estimateValue = estimateValue;
	}

	public double getEstimateValue() {
		return estimateValue;
	}

	public void setCoefficientOfDetermination(double coefficientOfDetermination) {
		this.coefficientOfDetermination = coefficientOfDetermination;
	}

	public double getCoefficientOfDetermination() {
		return coefficientOfDetermination;
	}

	public void setRegressionMethod(byte regressionMethod) {
		this.regressionMethod = regressionMethod;
	}

	public byte getRegressionMethod() {
		return regressionMethod;
	}

	public void setCreationDate(LiteDate creationDate) {
		this.creationDate = creationDate;
	}

	public LiteDate getCreationDate() {
		return creationDate;
	}

	public void setEstimateDate(LiteDate estimateDate) {
		this.estimateDate = estimateDate;
	}

	public LiteDate getEstimateDate() {
		return estimateDate;
	}

	public void setIndicators(ArrayList<IndicatorBean> indicators) {
		this.indicators = indicators;
	}

	public ArrayList<IndicatorBean> getIndicators() {
		return indicators;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}
	
	
}
