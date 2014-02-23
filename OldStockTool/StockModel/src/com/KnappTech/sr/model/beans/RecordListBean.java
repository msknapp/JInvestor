package com.KnappTech.sr.model.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.KnappTech.model.LiteDate;
import com.KnappTech.model.UpdateFrequency;
import com.KnappTech.sr.model.Regressable.Record;
import com.KnappTech.sr.model.Regressable.RecordList;
import com.KnappTech.sr.model.constants.SourceAgency;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;
import com.KnappTech.xml.SimpleNode;

public class RecordListBean implements NodeExpressable,Serializable {
	private static final long serialVersionUID = 201105151259L;
	protected ArrayList<Record> records = new ArrayList<Record>();
	protected RecordStatusBean status = null;
	
	public RecordListBean() {
		
	}
	
	public RecordListBean(RecordList<? extends Record> ls) {
		this.status = new RecordStatusBean(ls);
		for (Record r : ls.getRecords()) {
			this.records.add(r);
		}
	}
	
	public RecordListBean(INode node) {
		String format = "yyyyMMdd";
		INode statusNode = node.getSubNode(getStatusTypeName());
		if (statusNode==null) {
			// try using this node.
			statusNode = node;
		}
		this.status = makeStatus(statusNode);
		INode sn = node.getSubNode("rs");
		Collection<INode> rs = sn.getSubNodes("r");
		for (INode n : rs) {
			try {
				LiteDate d = LiteDate.getOrCreate(n.getSubNodeValue("d"), format);
				double v = Double.parseDouble(n.getSubNodeValue("v"));
				records.add(Record.create(d,v));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected String getStatusTypeName() {
		return RecordStatusBean.TYPENAME;
	}

	/**
	 * Child classes may make the status by different methods.
	 * This method should be overridden by child classes to use
	 * different implementations of the status bean.
	 * @param node
	 * @return
	 */
	protected RecordStatusBean makeStatus(INode node) {
		return new RecordStatusBean(node);
	}
	
	@Override
	public INode toNode() {
		if (status==null)
			return null;
		SimpleNode node = new SimpleNode(getTypeName());
		INode statusNode = status.toNode();
		node.addSubNode(statusNode);
		String format = "yyyyMMdd";
		SimpleNode sns = new SimpleNode("rs");
		node.addSubNode(sns);
		for (Record r : records) {
			try {
				SimpleNode rn = new SimpleNode("r");
				sns.addSubNode(rn);
				rn.addSubNode(new SimpleNode("d",r.getDate().getFormatted(format)));
				rn.addSubNode(new SimpleNode("v",String.valueOf(r.getValue())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return node;
	}
	
    protected String getTypeName() {
		return "RecordList";
	}

	public String getId() {
		return status.getId();
	}

	public void setId(String id) {
		status.setId(id);
	}

	public SourceAgency getSourceAgency() {
		return status.getSourceAgency();
	}

	public void setSourceAgency(SourceAgency sourceAgency) {
		status.setSourceAgency(sourceAgency);
	}

	public ArrayList<Record> getRecords() {
		return records;
	}

	public void setRecords(ArrayList<Record> records) {
		this.records = records;
	}

	public LiteDate getLastSuccessfulUpdate() {
		return status.getLastSuccessfulUpdate();
	}

	public void setLastSuccessfulUpdate(LiteDate lastSuccessfulUpdate) {
		status.setLastSuccessfulUpdate(lastSuccessfulUpdate);
	}

	public LiteDate getLastAttemptedUpdate() {
		return status.getLastAttemptedUpdate();
	}

	public void setLastAttemptedUpdate(LiteDate lastAttemptedUpdate) {
		status.setLastAttemptedUpdate(lastAttemptedUpdate);
	}

	public LiteDate getStartDate() {
		return status.getStartDate();
	}

	public void setStartDate(LiteDate startDate) {
		status.setStartDate(startDate);
	}

	public LiteDate getEndDate() {
		return status.getEndDate();
	}

	public void setEndDate(LiteDate endDate) {
		status.setEndDate(endDate);
	}

	public UpdateFrequency getUpdateFrequency() {
		return status.getUpdateFrequency();
	}

	public void setUpdateFrequency(UpdateFrequency updateFrequency) {
		status.setUpdateFrequency(updateFrequency);
	}

	public boolean isAdjusted() {
		return status.isAdjusted();
	}

	public void setAdjusted(boolean adjusted) {
		status.setAdjusted(adjusted);
	}
}