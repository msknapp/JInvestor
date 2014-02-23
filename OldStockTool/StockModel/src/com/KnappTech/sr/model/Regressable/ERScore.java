package com.KnappTech.sr.model.Regressable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.KnappTech.model.LiteDate;

public class ERScore {
	public static final String DATEFORMAT = "yyyyMMdd";
	public static final String SCORETAG = "sc";
	public static final String IDTAG = "id";
	public static final String TIMESUSEDTAG = "tu";
	public static final String STARTDATETAG = "sd";
	public static final String ENDDATETAG = "ed";
	public static final String NUMBEROFENTRIESTAG = "N";
	public static final String SIMILARENTRIESTAG = "se";
	public static final String ORDERTAG = "o";
	
	private final String id;
	private int timesUsed = 0;
	private LiteDate startDate;
	private LiteDate endDate;
	private int numberOfEntries=0;
	private Set<String> similarERs = new HashSet<String>();
	private int order = 999;

	public ERScore(String id) {
		if (id==null)
			throw new NullPointerException("id cannot be null/empty.");
		if (id.equals(""))
			throw new IllegalArgumentException("id cannot be null/empty.");
		this.id = id;
	}
	
	public ERScore(EconomicRecord er) {
		if (er==null)
			throw new NullPointerException("economic record cannot be null/empty.");
		this.id = er.getID();
		this.startDate = er.getStartDate();
		this.endDate = er.getEndDate();
		this.numberOfEntries=er.size();
	}
	
	public ERScore(Element entry) {
		this.id = entry.getElementsByTagName(IDTAG).item(0).getFirstChild().getNodeValue();
		if (this.id==null) 
			throw new NullPointerException("Could not determine id for an element.");
		Node node = null;
		String s = "";
		try {
			node = entry.getElementsByTagName(TIMESUSEDTAG).item(0).getFirstChild();
			if (node!=null) {
				s=node.getNodeValue();
				this.timesUsed = Integer.parseInt(s);
			}
			node = entry.getElementsByTagName(STARTDATETAG).item(0).getFirstChild();
			if (node!=null) {
				s=node.getNodeValue();
				this.startDate = LiteDate.getOrCreate(s, DATEFORMAT);
			}
			node = entry.getElementsByTagName(ENDDATETAG).item(0).getFirstChild();
			if (node!=null) {
				s=node.getNodeValue();
				this.endDate = LiteDate.getOrCreate(s, DATEFORMAT);
			}
			node = entry.getElementsByTagName(NUMBEROFENTRIESTAG).item(0).getFirstChild();
			if (node!=null) {
				s=node.getNodeValue();
				this.numberOfEntries = Integer.parseInt(s);
			}
			node = entry.getElementsByTagName(ORDERTAG).item(0).getFirstChild();
			if (node!=null) {
				s=node.getNodeValue();
				this.order = Integer.parseInt(s);
			}
			node = entry.getElementsByTagName(SIMILARENTRIESTAG).item(0).getFirstChild();
			if (node==null)
				return;
			s = node.getNodeValue();
			if (s.endsWith(","))
				s = s.substring(0,s.length()-1);
			String[] ss = s.split(",");
			for (String t : ss) {
				similarERs.add(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTimesUsed(int timesUsed) {
		this.timesUsed = timesUsed;
	}

	public int getTimesUsed() {
		return timesUsed;
	}
	
	public void incrementTimesUsed() {
		this.timesUsed++;
	}
	
	public String getID() {
		return id;
	}

	public void setStartDate(LiteDate startDate) {
		this.startDate = startDate;
	}

	public LiteDate getStartDate() {
		return startDate;
	}

	public void setNumberOfEntries(int numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}

	public int getNumberOfEntries() {
		return numberOfEntries;
	}

	public void setEndDate(LiteDate endDate) {
		this.endDate = endDate;
	}

	public LiteDate getEndDate() {
		return endDate;
	}
	
	public String toXML() {
		String s = "<"+SCORETAG+">";
		s+="<"+IDTAG+">"+id+"</"+IDTAG+">";
		s+="<"+ORDERTAG+">"+order+"</"+ORDERTAG+">";
		s+="<"+TIMESUSEDTAG+">"+timesUsed+"</"+TIMESUSEDTAG+">";
		s+="<"+STARTDATETAG+">"+(startDate!=null ? startDate.getFormatted(DATEFORMAT) : "")+"</"+STARTDATETAG+">";
		s+="<"+ENDDATETAG+">"+(endDate!=null ? endDate.getFormatted(DATEFORMAT) : "")+"</"+ENDDATETAG+">";
		s+="<"+NUMBEROFENTRIESTAG+">"+numberOfEntries+"</"+NUMBEROFENTRIESTAG+">";
		s+="<"+SIMILARENTRIESTAG+">";
		for (String t : this.similarERs) {
			s+=t+",";
		}
		s+="</"+SIMILARENTRIESTAG+">";
		s+="</"+SCORETAG+">";
		return s;
	}
	
	public boolean equal(Object o) {
		if (o==null)
			return false;
		if (!(o instanceof ERScore)) 
			return false;
		ERScore es = (ERScore)o;
		return this.getID().equals(es.getID());
	}
	
	public int hashCode() {
		return getID().hashCode()-2;
	}
	
	public String toString() {
		return getID()+": "+getTimesUsed();
	}
	
	public boolean isRedundant(String id) {
		return similarERs.contains(id);
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	public void markSimilar(String id2) {
		if (id2==null || id2.length()<1)
			return;
		this.similarERs.add(id2);
	}
	
	public boolean isUsed() {
		return timesUsed>0;
	}
	
	public Set<String> getSimilarERs() {
		return Collections.unmodifiableSet(similarERs);
	}
}