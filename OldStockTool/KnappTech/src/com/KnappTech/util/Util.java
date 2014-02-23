package com.KnappTech.util;

public class Util {

	public static boolean isMonthAbbreviation(String str) {
		if (str.toLowerCase().contains("jan") ||
			str.toLowerCase().contains("feb") || 
			str.toLowerCase().contains("mar") || 
			str.toLowerCase().contains("apr") || 
			str.toLowerCase().contains("may") || 
			str.toLowerCase().contains("jun") || 
			str.toLowerCase().contains("jul") || 
			str.toLowerCase().contains("aug") || 
			str.toLowerCase().contains("sep") || 
			str.toLowerCase().contains("oct") || 
			str.toLowerCase().contains("nov") || 
			str.toLowerCase().contains("dec")) 
		{
			return true;
		}
		return false;
	}
	
	public static int getMonthFromAbbreviation(String str) {
		if (str.toLowerCase().contains("jan")) {
			return 0;
		} else if (str.toLowerCase().contains("feb")) {
			return 1;
		} else if (str.toLowerCase().contains("mar")) {
			return 2;
		} else if (str.toLowerCase().contains("apr")) {
			return 3;
		} else if (str.toLowerCase().contains("may")) {
			return 4;
		} else if (str.toLowerCase().contains("jun")) {
			return 5;
		} else if (str.toLowerCase().contains("jul")) {
			return 6;
		} else if (str.toLowerCase().contains("aug")) {
			return 7;
		} else if (str.toLowerCase().contains("sep")) {
			return 8;
		} else if (str.toLowerCase().contains("oct")) {
			return 9;
		} else if (str.toLowerCase().contains("nov")) {
			return 10;
		} else if (str.toLowerCase().contains("dec")) {
			return 11;
		}
		return -1;
	}
}
