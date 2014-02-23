package com.KnappTech.xml;

import org.junit.Assert;
import org.junit.Test;

public class XMLTest {
	
	private String textXML = "<hi>\n<!-- blah -->\t<yippee wow=\"yes\">my value</yippee><zip hi='t\"'></zip><br/></hi>";
	
	@Test
	public void testParse() {
		SimpleNode n = NodeParser.parse(textXML);
		System.out.println("Original:\n"+textXML);
		System.out.println("Produced:\n"+n);
		Assert.assertTrue(n!=null);
	}

}
