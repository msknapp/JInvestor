package jinvestor.jhouse.core.parboiled;

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

import jinvestor.jhouse.core.parboiled.QueryNodeParser;
import jinvestor.jhouse.core.query.QueryNode;
import jinvestor.jhouse.core.query.QueryPart;

import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

public class QueryNodeParserTest {

	@Test
	public void parse() {
		String input = "foo='bar ( testing! ' and (a='b\"()' or c<'8') ";
		QueryNodeParser parser = Parboiled.createParser(QueryNodeParser.class);
		ParsingResult<QueryNode> result = ReportingParseRunner.run(
				parser.root(), input);
		QueryNode qn = result.resultValue;
		while (qn.parent!=null) {
			qn = qn.parent;
		}
		QueryPart qp = qn.toQueryPart();
		System.out.println(qp.toString());
		
		
	}
}
