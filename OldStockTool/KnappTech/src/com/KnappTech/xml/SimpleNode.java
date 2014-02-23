package com.KnappTech.xml;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class SimpleNode implements INode {

	private String name;
	private String value;
	private final Hashtable<String,String> attributes = new Hashtable<String,String>();
	private final List<INode> subNodes = new ArrayList<INode>();
	private INode parent = null;
	
	public SimpleNode() {
		this.setName(null);
		this.setValue(null);
	}
	
	public SimpleNode(String name) {
		setName(name);
		setValue(null);
	}
	
	public SimpleNode(String name,String value) {
		setName(name);
		setValue(value);
	}
	
	public static SimpleNode parse(String xml) {
		return NodeParser.parse(xml);
	}
	
	/* (non-Javadoc)
	 * @see com.KnappTech.xml.INode#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.KnappTech.xml.INode#getValue()
	 */
	@Override
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see com.KnappTech.xml.INode#getAttributes()
	 */
	@Override
	public Hashtable<String,String> getAttributes() {
		return attributes;
	}

	/* (non-Javadoc)
	 * @see com.KnappTech.xml.INode#getSubNodes()
	 */
	@Override
	public List<INode> getSubNodes() {
		return subNodes;
	}
	
	/* (non-Javadoc)
	 * @see com.KnappTech.xml.INode#getSubNode(java.lang.String)
	 */
	@Override
	public INode getSubNode(String name) {
		for (INode n : subNodes)
			if (n.getName().equals(name))
				return n;
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.KnappTech.xml.INode#getSubNodes(java.lang.String)
	 */
	@Override
	public List<INode> getSubNodes(String name) {
		List<INode> sn = new ArrayList<INode>();
		for (INode n : subNodes)
			if (n.getName().equals(name))
				sn.add(n);
		return sn;
	}
	
	public INode addSubNode(INode n) {
		if (n==null)
			return null;
		this.subNodes.add(n);
		return n;
	}
	
	public INode addSubNode(String name,Object value) {
		if (name==null || name.length()<1 || name.equals("null"))
			return null;
		SimpleNode n = new SimpleNode(name,value.toString());
		this.subNodes.add(n);
		return n;
	}
	
	public void removeSubNodes(String name) {
		Iterator<INode> iter = subNodes.iterator();
		INode n = null;
		while (iter.hasNext()) {
			n = iter.next();
			if (n.getName().equals(name)) 
				iter.remove();
		}
	}
	
	public void addAttribute(String name,String value) {
		attributes.put(name,value);
	}

	public void removeAttribute(String name) {
		attributes.remove(name);
	}
	
	/* (non-Javadoc)
	 * @see com.KnappTech.xml.INode#toXML()
	 */
	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(name);
		String val = "";
		String qt = "\"";
		for (String key : attributes.keySet()) {
			sb.append(" "+key+"=");
			val = attributes.get(key);
			qt = "\"";
			if (val.contains("\""))
				qt="'";
			sb.append(qt+val+qt);
		}
		if (getSubNodes().size()<1 && value==null) {
			sb.append("/>");
			return sb.toString();
		} else {
			sb.append(">");
		}
		if (value!=null)
			sb.append(EncodeHelper.encode(getValue()));
		for (INode sn : subNodes) {
			sb.append(sn.toXML());
		}
		sb.append("</"+getName()+">");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.KnappTech.xml.INode#toString()
	 */
	@Override
	public String toString() {
		return toXML();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setParent(INode parent) {
		this.parent = parent;
	}

	public INode getParent() {
		return parent;
	}

	@Override
	public INode toNode() {
		return this;
	}

	@Override
	public String getSubNodeValue(String name) {
		INode n = getSubNode(name);
		if (n==null)
			return "";
		return n.getValue();
	}
}