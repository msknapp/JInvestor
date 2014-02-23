package com.KnappTech.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class SimpleSAJHandler extends DefaultHandler {
	private SimpleNode node = null;
	private StringBuilder sb = null;
	private SimpleNode root =null;
	
	public SimpleSAJHandler() {}
	
	@Override
	public void startElement(String uri,String localName,String qName,Attributes attributes) {
		if (qName==null || qName.length()<1)
			return;
		if (node==null) {
			node = new SimpleNode(qName);
			root = node;
		} else {
			SimpleNode sn = new SimpleNode(qName);
			sn.setParent(node);
			node.addSubNode(sn);
			node = sn;
		}
		for (int i = 0;i<attributes.getLength();i++) {
			node.addAttribute(attributes.getLocalName(i),attributes.getValue(i));
		}
	}
	
	@Override
	public void endElement(String uri,String localName,String qNames) {
		if (sb!=null)
			node.setValue(EncodeHelper.decode(sb.toString()));
		node = (SimpleNode) node.getParent();
		sb = null;
	}
	
	@Override
	public void characters(char[] c,int start,int length) {
		if (sb==null)
			sb = new StringBuilder();
		char[] mychars = new char[length];
		System.arraycopy(c, start, mychars, 0, length);
		String s= new String(mychars);
		sb.append(s);
	}
	
	public SimpleNode getRoot() {
		return root;
	}
	
	public void warning(SAXParseException e) {
		// do not report it, just continue.
		System.out.println("  :\\  Had warning exception in xml, "+e.getMessage());
	}
	
	public void error(SAXParseException e) {
		// do not report it, just continue.
		System.out.println("  :\\  Had error exception in xml, "+e.getMessage());
	}
	
}