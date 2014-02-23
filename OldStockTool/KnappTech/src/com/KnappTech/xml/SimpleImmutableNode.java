package com.KnappTech.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SimpleImmutableNode implements INode {
	private final String name;
	private final String value;
	private final Map<String,String> attributes;
	private final List<INode> subNodes;
	private final INode parent;
	
	public SimpleImmutableNode(INode node) {
		this(node,null);
	}
	
	public SimpleImmutableNode(INode node,INode parent) {
		this.name = node.getName();
		this.value = node.getValue();
		this.parent = parent;
		this.attributes = Collections.unmodifiableMap(node.getAttributes());
		List<INode> tmp = new ArrayList<INode>();
		for (INode nd : node.getSubNodes()) {
			tmp.add(new SimpleImmutableNode(nd,this));
		}
		subNodes = Collections.unmodifiableList(tmp);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public Map<String, String> getAttributes() {
		return attributes;
	}

	@Override
	public INode getSubNode(String name) {
		for (INode nd : subNodes) 
			if (nd.getName().equals(name))
				return nd;
		return null;
	}

	@Override
	public List<INode> getSubNodes() {
		return subNodes;
	}

	@Override
	public List<INode> getSubNodes(String name) {
		List<INode> tmp = new ArrayList<INode>();
		for (INode nd : subNodes) 
			if (nd.getName().equals(name))
				tmp.add(nd);
		return Collections.unmodifiableList(tmp);
	}

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
			sb.append(getValue());
		for (INode sn : subNodes) {
			sb.append(sn.toXML());
		}
		sb.append("</"+getName()+">");
		return sb.toString();
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