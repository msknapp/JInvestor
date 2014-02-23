package com.KnappTech.util;

//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Collection;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

//import com.knappTech.beans.CustomPersistable;

public class DataUtility {
	public static final transient String EIGHTZEROES = "00000000";
	public static final transient String EIGHTONES = "11111111";
	public static final transient String CODEFORNULL = "00000001";
	public static final transient String CODEFORZERO = "00000010";

	public static String getAttributeValue(String text, String attribute) {
		int len = attribute.length();
		int beginIndex = text.indexOf("<"+attribute+">")+len+2;
		int endIndex = beginIndex+1;
		endIndex = getEndIndex(text, attribute,beginIndex);
		return text.substring(beginIndex, endIndex);
	}

	/**
	 * Should get a list of sub attributes, including the attribute names themselves.
	 * It is smart enough that if a nested attribute has the same name, it will not
	 * get confused and return part of its value.  It should return its whole value,
	 * despite children with the same attribute name.
	 * @param text
	 * @param attribute
	 * @return
	 */
	public static LinkedHashSet<String> getStringList(String text, String attribute) {
		LinkedHashSet<String> ans = new LinkedHashSet<String>();
		if (!text.isEmpty() && text.length()>6){
			int len = attribute.length();
			int beginIndex = len+2;
			int endIndex = 0;
			int count = 0;
			while (beginIndex >= len+2 && count<20000) {
				beginIndex = text.indexOf("<"+attribute+">",endIndex)+len+2;
				if (beginIndex>=len+2){
					endIndex = getEndIndex(text,attribute,beginIndex);
					String str = text.substring(beginIndex, endIndex);
					ans.add(str);
				}
				count++;
			}
		}
		return ans;
	}
	
//	public static String deflateCollection(Collection<? extends Object> collection, String elementName, FileOutputStream stream){
//		String results = "";
//		FileWriter fw = null;
//		try {
//			fw = new FileWriter(stream);
//		}
//		for (Object element : collection) {
//			try {
//				results += "<"+elementName+">"+element.toString()+"</"+elementName+">";
//				byte[] bytes = results.getBytes();
//				stream.write(bytes);
//			} catch (IOException e) {
//				System.err.println("IOException caught while deflating a collection.");
//			}
//		}
//		return results;
//	}
//	
//	public static String deflateCPCollection(Collection<? extends CustomPersistable> collection, String elementName, FileOutputStream stream){
//		String results = "";
//		if (collection != null) {
//			for (CustomPersistable element : collection) {
//				results += "<"+elementName+">"+element.deflate()+"</"+elementName+">";
//			}
//		} else {
//			return "<"+elementName+"></"+elementName+">";
//		}
//		return results;
//	}
//	
//	public static String deflateCPMap(Map<String,? extends CustomPersistable> map, String elementName){
//		String results = "";
//		Set<String> keys = map.keySet();
//		for (String key : keys) {
//			results += "<"+elementName+">";
//			results += "<k>"+key+"</k>";
//			results += "<v>"+map.get(key).deflate()+"</v>";
//			results += "</"+elementName+">";
//		}
//		return results;
//	}
//	
//	public static String deflateCPCPMap(Map<? extends CustomPersistable,? extends CustomPersistable> map, String elementName){
//		String results = "";
//		Set<? extends CustomPersistable> keys = map.keySet();
//		for (CustomPersistable key : keys) {
//			results += "<"+elementName+">";
//			results += "<k>"+key.deflate()+"</k>";
//			results += "<v>"+map.get(key).deflate()+"</v>";
//			results += "</"+elementName+">";
//		}
//		return results;
//	}
	
	public static String deflateStringMap(Map<String,String> map, String elementName){
		String results = "";
		Set<String> keys = map.keySet();
		for (String key : keys) {
			results += "<"+elementName+">";
			results += "<k>"+key+"</k>";
			results += "<v>"+map.get(key)+"</v>";
			results += "</"+elementName+">";
		}
		return results;
	}
	
	public static String deflatePositiveInt(Integer num, byte charsPerValue) {
		char[] chars = new char[charsPerValue];
		if (num>0){
			String bits = Integer.toBinaryString(num);
			while (bits.length()<7*charsPerValue) {
				bits = "0"+bits;
			}
			String part = null;
			for (int i = 0;i<charsPerValue;i++){
				part = "1"+bits.substring(7*i, 7*(i+1));
				chars[i] = eightBitsToChar(part);
			}
		} else { // This represents a null value in the price history
			for (int i = 0;i<charsPerValue;i++){
				chars[i] = eightBitsToChar(CODEFORNULL);
			}
		}
		return new String(chars);
	}
	
	public static int inflatePositiveInt(String ret) {
		char nullChar = eightBitsToChar(CODEFORNULL);
		boolean isNullPrice = true;
		for (int i = 0;i<ret.length();i++) {
			char c = ret.charAt(i);
			if (c!=nullChar){
				isNullPrice = false;
				break;
			}
		}
		if (!isNullPrice) {
			String bits = "";
			for (int i = 0;i<ret.length();i++){
				char thischar = ret.charAt(i);
				String part = Integer.toBinaryString(thischar);
				bits += part.substring(part.length()-7);
			}
			return bitsToInt(bits);
		} else {
			return -1;
		}
	}
	
	private static int bitsToInt(String bits) {
		int val = 0;
		int size = bits.length();
		for (int i = 0;i<size;i++){
			String s = String.valueOf(bits.charAt(i));
			byte bit = Byte.parseByte(s);
			val += bit*Math.pow(2,size-i-1);
		}
		return val;
	}
	
	private static int sixteenBitsToInt(String bits) {
		int val = 0;
		for (int i = 0;i<16;i++){
			String s = String.valueOf(bits.charAt(i));
			byte bit = Byte.parseByte(s);
			val += bit*Math.pow(2,15-i);
		}
		return val;
	}
	
	private static byte eightBitsToByte(String bits) {
		byte val = 0;
		for (int i = 0;i<8;i++){
			String s = String.valueOf(bits.charAt(i));
			byte bit = Byte.parseByte(s);
			val += bit*Math.pow(2,7-i);
		}
		return val;
	}

	private static char eightBitsToChar(String bits) {
		return (char)eightBitsToByte(bits);
	}

//	private static int thirtytwoBitsToInt(String bits) {
//		int val = 0;
//		for (int i = 1;i<32;i++){
//			String s = String.valueOf(bits.charAt(i));
//			byte bit = Byte.parseByte(s);
//			val+= Math.pow(2, (32-i)*bit);
//		}
//		String s = String.valueOf(bits.charAt(0));
//		byte bit = Byte.parseByte(s);
//		if (bit==1){
//			val*=-1;
//		}
//		return val;
//	}
	
	public static String doubleToBinaryString(Double dbl) {
		long bla = Double.doubleToLongBits(dbl);
		String bits = Long.toBinaryString(bla);
		String firstBits = bits.substring(0,16);
		String secondBits = bits.substring(16,32);
		String thirdBits = bits.substring(32,48);
		String fourthBits = bits.substring(48,64);
		int fbi = sixteenBitsToInt(firstBits);
		int sbi = sixteenBitsToInt(secondBits);
		int tbi = sixteenBitsToInt(thirdBits);
		int rbi = sixteenBitsToInt(fourthBits);
		char fChar = Character.toChars(fbi)[0];
		char sChar = Character.toChars(sbi)[0];
		char tChar = Character.toChars(tbi)[0];
		char rChar = Character.toChars(rbi)[0];
		char[] value = {fChar, sChar, tChar, rChar};
		String ret = new String(value);
		return ret;
		// TODO Check that the double converts to binary string correctly.
	}
	
	public static Double binaryStringToDouble(String str) {
		char fChar = str.charAt(0);
		char sChar = str.charAt(1);
		char tChar = str.charAt(2);
		char rChar = str.charAt(3);
		char[] fcs = {fChar};
		char[] scs = {sChar};
		char[] tcs = {tChar};
		char[] rcs = {rChar};
		Integer fbi = Character.codePointAt(fcs, 0);
		Integer sbi = Character.codePointAt(scs, 0);
		Integer tbi = Character.codePointAt(tcs, 0);
		Integer rbi = Character.codePointAt(rcs, 0);
		String fbits = Integer.toBinaryString(fbi);
		String sbits = Integer.toBinaryString(sbi);
		String tbits = Integer.toBinaryString(tbi);
		String rbits = Integer.toBinaryString(rbi);
		String binaryString = fbits+sbits+tbits+rbits;
		long bits = Long.parseLong(binaryString);
		double res = Double.longBitsToDouble(bits);
		return res;
		// TODO Check that the binary string converts to double correctly.
	}
	
	public static String makeIntMinLength(Integer num, int length) {
		String st = num.toString();
		int len = st.length();
		if (len == length) {
			return st;
		} else if (len < length) {
			int zer = length - len;
			String s = "";
			for (int i = 0;i<zer; i++){
				s += "0";
			}
			s += st;
			return s;
		} else {
			return st;
		}
	}

	public static int getEndIndex(String text, String attribute, int startIndex) {
		text=text.toLowerCase();
		attribute = attribute.toLowerCase();
		int endIndex = startIndex;
		int level = 1;
		while (level>0) {
			endIndex = text.indexOf(attribute+">", endIndex+1);
			if (endIndex>-1) {
				if (text.charAt(endIndex-1)=='/'){
					level--;
				} else if (text.charAt(endIndex-1)=='<'){
					level++;
				}
			} else {
				throw new RuntimeException("Could not find the end of the attribute in a line of text.");
			}
		}
		return endIndex-2;
	}
	
	public static LinkedHashSet<String> getLetterSubset(Collection<String> coll,char letter) {
		LinkedHashSet<String> results = new LinkedHashSet<String>();
 		for (String id : coll) {
 			if (id.charAt(0)==letter) {
 				results.add(id);
 			}
 		}
 		return results;
	}
}
