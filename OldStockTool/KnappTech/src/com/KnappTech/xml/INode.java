package com.KnappTech.xml;

import java.util.List;
import java.util.Map;

public interface INode extends NodeExpressable {
	public String getName();
	public String getValue();
	public Map<String, String> getAttributes();
	public INode getSubNode(String name);
	public String getSubNodeValue(String name);
	public List<INode> getSubNodes();
	public List<INode> getSubNodes(String name);
	public String toXML();
	public INode getParent();
}