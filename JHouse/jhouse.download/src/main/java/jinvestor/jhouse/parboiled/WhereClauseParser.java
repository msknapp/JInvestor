package jinvestor.jhouse.parboiled;

import jinvestor.jhouse.query.ComplexQueryPart;
import jinvestor.jhouse.query.QueryNode;
import jinvestor.jhouse.query.QueryPartBuilder;
import jinvestor.jhouse.query.ComplexQueryPart.ComplexQueryPartBuilder;

import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.Rule;

public class WhereClauseParser extends BaseParser<QueryPartBuilder> {
	boolean inQuotes = false;
	char quoteChar = 'x';

	Rule singleQuote() {
		return Sequence(Ch('\''), new Action<QueryPartBuilder>() {

			@Override
			public boolean run(Context<QueryPartBuilder> context) {
				if (inQuotes) {
					if (quoteChar=='\'') {
						inQuotes=false;
						quoteChar='x';
					}
				} else {
					inQuotes=true;
					quoteChar='\'';
				}
				return true;
			}
		});
	}

	Rule doubleQuote() {
		return Sequence(Ch('"'),new Action<QueryNode>() {

			@Override
			public boolean run(Context<QueryNode> context) {
				if (inQuotes) {
					if (quoteChar=='\"') {
						inQuotes=false;
						quoteChar='x';
					}
				} else {
					inQuotes=true;
					quoteChar='\"';
				}
				return true;
			}
		});
	}

	Rule openParen() {
		return Sequence(Ch('('),new Action<QueryPartBuilder>() {
			@Override
			public boolean run(Context<QueryPartBuilder> context) {
				if (!inQuotes) {
					ComplexQueryPartBuilder b = ComplexQueryPart.builder();
					
					context.getValueStack().push(b);
				}
				return true;
			}
		});
	}

	Rule closeParen() {
		return Sequence(Ch(')'),new Action<QueryPartBuilder>(){

			@Override
			public boolean run(Context<QueryPartBuilder> context) {
				if (!inQuotes) {
					
				}
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	Rule qOr() {
		return IgnoreCase("or");
	}

	Rule qAnd() {
		return IgnoreCase("and");
	}

	Rule whitespace() {
		return OneOrMore(AnyOf("\n\r\t "));
	}

	Rule eq() {
		return Ch('=');
	}

	Rule text() {
		return OneOrMore(FirstOf(CharRange('a', 'z'),CharRange('A','Z'),Ch('_'),CharRange('0','9')));
	}

	Rule root() {
		return Sequence(new Action<QueryPartBuilder>(){

			@Override
			public boolean run(Context<QueryPartBuilder> context) {
				if (context.getValueStack().isEmpty()) {
					context.getValueStack().push(ComplexQueryPart.builder());
				}
				return true;
			}
		},OneOrMore(FirstOf(singleQuote(), doubleQuote(), openParen(),
				closeParen(), eq(), qOr(), qAnd(), whitespace(), text())));
	}
}
