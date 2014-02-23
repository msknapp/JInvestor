package com.KnappTech.sr.model.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.KnappTech.sr.model.comp.RegressionResult;
import com.KnappTech.sr.model.comp.RegressionResults;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;
import com.KnappTech.xml.SimpleNode;

public class RegressionResultsBean implements Serializable, NodeExpressable {
	public static final long serialVersionUID = 201102131349L; 
	private ArrayList<RegressionResultBean> regressionResults = 
		new ArrayList<RegressionResultBean>();
	
	public RegressionResultsBean() {
		
	}
	
	public RegressionResultsBean(RegressionResults rr) {
		if (rr!=null) {
			List<RegressionResult> rl = rr.getItems();
			for (RegressionResult r : rl) {
				regressionResults.add(new RegressionResultBean(r));
			}
		}
	}
	public RegressionResultsBean(INode n) {
		Collection<INode> sns = n.getSubNodes();
		for (INode sn : sns) {
			regressionResults.add(new RegressionResultBean(sn));
		}
	}

	@Override
	public INode toNode() {
		SimpleNode node = new SimpleNode("rrs");
		for (RegressionResultBean b : regressionResults) {
			node.addSubNode(b.toNode());
		}
		return node;
	}

	public void setRegressionResults(ArrayList<RegressionResultBean> regressionResults) {
		this.regressionResults = regressionResults;
	}

	public ArrayList<RegressionResultBean> getRegressionResults() {
		return regressionResults;
	}
}