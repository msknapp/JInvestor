package com.KnappTech.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class NodeParser {
	
	public static final SimpleNode parse(String xml) {
		SimpleSAJHandler p = new SimpleSAJHandler();
		SAXParserFactory f = SAXParserFactory.newInstance();
		InputStream is = null;
		try {
			SAXParser s = f.newSAXParser();
			is = new ByteArrayInputStream(xml.getBytes());
			s.parse(is, p);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
			if (is!=null)
				try { is.close(); } catch (Exception e) {e.printStackTrace();}
		}
		return p.getRoot();
	}
}