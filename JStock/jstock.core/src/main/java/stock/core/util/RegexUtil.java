package stock.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtil {

	private RegexUtil(){
		
	}
	
	public static final String getFirstMatch(String text,String regex) {
		return getFirstMatch(text, regex, 0,-1);
	}
	
	public static final String getFirstMatch(String text,String regex,int group) {
		return getFirstMatch(text, regex, group,-1);
	}
	
	public static final String getFirstMatch(String text,String regex,int group,int flags) {
		Pattern pattern = null;
		if (flags<0) {
			pattern = Pattern.compile(regex);
		} else {
			pattern = Pattern.compile(regex,flags);
		}
		Matcher matcher = pattern.matcher(text);
		String found = null;
		if (matcher.find()) {
			found = matcher.group(group);
		}
		return found;
	}
	
	public static final List<String> getAllMatches(String text,String regex,int group,int flags) {
		Pattern pattern = null;
		if (flags<0) {
			pattern = Pattern.compile(regex);
		} else {
			pattern = Pattern.compile(regex,flags);
		}
		Matcher matcher = pattern.matcher(text);
		List<String> matches = new ArrayList<String>();
		while (matcher.find()) {
			matches.add(matcher.group(group));
		}
		return matches;
	}
	
}