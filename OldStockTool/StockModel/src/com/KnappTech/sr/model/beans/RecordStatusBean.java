package com.KnappTech.sr.model.beans;

import java.util.Calendar;

import com.KnappTech.model.Identifiable;
import com.KnappTech.model.LiteDate;
import com.KnappTech.model.UpdateFrequency;
import com.KnappTech.sr.model.Regressable.Record;
import com.KnappTech.sr.model.Regressable.RecordList;
import com.KnappTech.sr.model.constants.SourceAgency;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;
import com.KnappTech.xml.SimpleNode;

public class RecordStatusBean implements NodeExpressable, Identifiable<String> {
	private static final long serialVersionUID = 201105281019L;
	public static final String TYPENAME = "st";
	protected String id=null;
	protected SourceAgency sourceAgency=null;
    protected LiteDate lastSuccessfulUpdate = null;
	protected LiteDate lastAttemptedUpdate = null;
	protected LiteDate startDate = null;
	protected LiteDate endDate = null;
	protected UpdateFrequency updateFrequency = null;
	private double lastValue = 0;
	private double lastDerivativeValue = 0;
	protected boolean adjusted = false;
	private int size = 0;
	private double volatility = 0;

	public RecordStatusBean() {
		
	}
	
	public RecordStatusBean(RecordList<? extends Record> ls) {
		this.id = ls.getID();
		this.sourceAgency = ls.getSourceAgency();
		this.lastSuccessfulUpdate = ls.getLastSuccessfulUpdate();
		this.lastAttemptedUpdate = ls.getLastAttemptedUpdate();
		this.startDate = ls.getStartDate();
		this.endDate = ls.getEndDate();
		this.updateFrequency = ls.getUpdateFrequency();
		this.adjusted = ls.isAdjusted();
		this.lastValue = ls.getLastValue();
		this.lastDerivativeValue = ls.getLastValue()-ls.getValueAtIndex(ls.size()-2);
		this.size = ls.size();
		this.volatility = ls.calculateVolatility(LiteDate.getOrCreate(Calendar.YEAR, -3));
	}
	
	public RecordStatusBean(INode node) {
		if (node==null)
			throw new NullPointerException("Must define a node to build this bean.");
		if (node.getSubNode("id")==null) {
			// need to find the right node.
			for (INode n : node.getSubNodes()) {
				if (n.getSubNode("id")!=null) {
					node=n;
					break;
				}
			}
		}
		String format = "yyyyMMdd";
		this.id = node.getSubNode("id").getValue();
		this.sourceAgency = SourceAgency.parse(node.getSubNodeValue("sa"));
		this.lastSuccessfulUpdate = LiteDate.getOrCreate(node.getSubNodeValue("lsu"), format);
		this.lastAttemptedUpdate = LiteDate.getOrCreate(node.getSubNodeValue("lau"), format);
		this.endDate = LiteDate.getOrCreate(node.getSubNodeValue("ed"), format);
		this.startDate = LiteDate.getOrCreate(node.getSubNodeValue("sd"), format);
		this.updateFrequency = UpdateFrequency.parse(node.getSubNodeValue("uf"));
		this.adjusted = Boolean.parseBoolean(node.getSubNodeValue("adj"));
		String s = node.getSubNodeValue("lv");
		if (s!=null && s.length()>0)
			this.lastValue = Double.parseDouble(s);
		s=node.getSubNodeValue("ldv");
		if (s!=null && s.length()>0)
			this.lastDerivativeValue = Double.parseDouble(s);
		s = node.getSubNodeValue("sz");
		if (s!=null && s.length()>0)
			this.size = Integer.parseInt(s);
		s = node.getSubNodeValue("vol");
		if (s!=null && s.length()>0)
			this.volatility = Double.parseDouble(s);
	}
	
	@Override
	public INode toNode() {
		String format = "yyyyMMdd";
		SimpleNode node = new SimpleNode(getTypeName());
		node.addSubNode(new SimpleNode("id",id));
		node.addSubNode(new SimpleNode("sa",sourceAgency.name()));
		node.addSubNode(new SimpleNode("lsu",lastSuccessfulUpdate.getFormatted(format)));
		node.addSubNode(new SimpleNode("lau",lastAttemptedUpdate.getFormatted(format)));
		node.addSubNode(new SimpleNode("sd",startDate.getFormatted(format)));
		node.addSubNode(new SimpleNode("ed",endDate.getFormatted(format)));
		node.addSubNode(new SimpleNode("uf",updateFrequency.name()));
		node.addSubNode(new SimpleNode("adj",adjusted ? "t" : "f"));
		node.addSubNode(new SimpleNode("lv",String.valueOf(lastValue)));
		node.addSubNode(new SimpleNode("ldv",String.valueOf(lastDerivativeValue)));
		node.addSubNode(new SimpleNode("sz",String.valueOf(size)));
		node.addSubNode(new SimpleNode("vol",String.valueOf(volatility)));
		return node;
	}
	
    public String getTypeName() {
		return TYPENAME;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SourceAgency getSourceAgency() {
		return sourceAgency;
	}

	public void setSourceAgency(SourceAgency sourceAgency) {
		this.sourceAgency = sourceAgency;
	}

	public LiteDate getLastSuccessfulUpdate() {
		return lastSuccessfulUpdate;
	}

	public void setLastSuccessfulUpdate(LiteDate lastSuccessfulUpdate) {
		this.lastSuccessfulUpdate = lastSuccessfulUpdate;
	}

	public LiteDate getLastAttemptedUpdate() {
		return lastAttemptedUpdate;
	}

	public void setLastAttemptedUpdate(LiteDate lastAttemptedUpdate) {
		this.lastAttemptedUpdate = lastAttemptedUpdate;
	}

	public LiteDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LiteDate startDate) {
		this.startDate = startDate;
	}

	public LiteDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LiteDate endDate) {
		this.endDate = endDate;
	}

	public UpdateFrequency getUpdateFrequency() {
		return updateFrequency;
	}

	public void setUpdateFrequency(UpdateFrequency updateFrequency) {
		this.updateFrequency = updateFrequency;
	}

	public boolean isAdjusted() {
		return adjusted;
	}

	public void setAdjusted(boolean adjusted) {
		this.adjusted = adjusted;
	}

	@Override
	public int compareTo(String o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getID() {
		return this.id;
	}

	public void setLastValue(double lastValue) {
		this.lastValue = lastValue;
	}

	public double getLastValue() {
		return lastValue;
	}

	public void setLastDerivativeValue(double lastDerivativeValue) {
		this.lastDerivativeValue = lastDerivativeValue;
	}

	public double getLastDerivativeValue() {
		return lastDerivativeValue;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public void setVolatility(double volatility) {
		this.volatility = volatility;
	}

	public double getVolatility() {
		return volatility;
	}
}