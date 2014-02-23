package jinvestor.jhouse.parboiled;

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
