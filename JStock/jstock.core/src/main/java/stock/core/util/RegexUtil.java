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