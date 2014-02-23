package jinvestor.jhouse.parboiled;

import jinvestor.jhouse.parboiled.QueryNodeParser;
import jinvestor.jhouse.query.QueryNode;
import jinvestor.jhouse.query.QueryPart;

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
