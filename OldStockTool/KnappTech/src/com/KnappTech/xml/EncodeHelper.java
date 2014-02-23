package com.KnappTech.xml;

public class EncodeHelper {
	
	public static String encode(String text) {
		text = text.replace("<","&lt;");
		text = text.replace(">","&gt;");
		text = text.replace("&","&amp;");
		text = text.replace("\"","&quot;");
		return text;
	}
	
	public static String decode(String text) {
		text = text.replace("&lt;","<");
		text = text.replace("&gt;",">");
		text = text.replace("&amp;","&");
		text = text.replace("&quot;","\"");
		return text;
	}
}
