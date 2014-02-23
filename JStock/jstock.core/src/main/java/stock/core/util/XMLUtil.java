package stock.core.util;

/*
 * #%L
 * jinvestor.parent
 * %%
 * Copyright (C) 2014 Michael Scott Knapp
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class XMLUtil {
	private static final Logger logger = Logger.getLogger(XMLUtil.class);

	private static final XPath xpath;
	private static final DocumentBuilder documentBuilder;
	
	static {
		xpath = XPathFactory.newInstance().newXPath();
		DocumentBuilder db = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringComments(true);
			dbf.setValidating(false);
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage(),e);
		}
		documentBuilder = db;
	}
	
	private XMLUtil(){}
	
	public static final NodeList getNodes(Node root,String expression) {
		XPathExpression xpe;
		NodeList nl = null;
		try {
			xpe = XMLUtil.xpath.compile(expression);
			nl = (NodeList) xpe.evaluate(root, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			logger.error(e.getMessage(),e);
		}
		return nl;
	}
	
	public static final Node getNode(Node root,String expression) {
		XPathExpression xpe;
		Node nl = null;
		try {
			xpe = XMLUtil.xpath.compile(expression);
			nl = (Node) xpe.evaluate(root, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			logger.error(e.getMessage(),e);
		}
		return nl;
	}
	
	public static String toString(Node node) {
		StringWriter writer = new StringWriter(); 
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(node), new StreamResult(writer));
		} catch (TransformerConfigurationException e) {
			logger.error(e.getMessage(),e);
		} catch (TransformerFactoryConfigurationError e) {
			logger.error(e.getMessage(),e);
		} catch (TransformerException e) {
			logger.error(e.getMessage(),e);
		}
		return writer.toString();
	}
	
	public static Document getDocument(InputStream in) {
		Document doc = null;
		try {
			doc = documentBuilder.parse(in);
		} catch (SAXException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return doc;
	}
}