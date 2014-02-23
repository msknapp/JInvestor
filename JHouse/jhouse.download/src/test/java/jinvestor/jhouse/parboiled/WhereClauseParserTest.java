package jinvestor.jhouse.parboiled;

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

import jinvestor.jhouse.parboiled.WhereClauseParser;
import jinvestor.jhouse.query.QueryPart;
import jinvestor.jhouse.query.QueryPartBuilder;

import org.junit.Ignore;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

public class WhereClauseParserTest {

	@Test
	@Ignore("The where clause parser does not work, use the QueryNodeParser instead.")
	public void parse() {
		String input = "";
		WhereClauseParser parser = Parboiled.createParser(WhereClauseParser.class);
		ParsingResult<QueryPartBuilder> result = ReportingParseRunner.run(parser.root(), input);
		QueryPartBuilder b = result.valueStack.peek();
		QueryPart p = b.build();
		
	}
}
