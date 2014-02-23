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

import jinvestor.jhouse.query.QueryNode;

import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.Rule;

public class QueryNodeParser extends BaseParser<QueryNode> {
	private static final String WHITESPACE = "\n\r\t ";
	StringBuilder sb = new StringBuilder();

	Rule singleQuote() {
		return Ch('\'');
	}

	Rule doubleQuote() {
		return Ch('"');
	}

	Rule singleQuotedText() {
		return Sequence(singleQuote(), ZeroOrMore(NoneOf("'")), singleQuote());
	}

	Rule doubleQuotedText() {
		return Sequence(doubleQuote(), ZeroOrMore(NoneOf("\"")), doubleQuote());
	}

	Rule quotedText() {
		return Sequence(FirstOf(singleQuotedText(), doubleQuotedText()),
				new Action<QueryNode>() {

					@Override
					public boolean run(Context<QueryNode> context) {
						sb.append(context.getMatch());
						return true;
					}
				});
	}

	Rule openParen() {
		return Sequence(Ch('('), new Action<QueryNode>() {
			@Override
			public boolean run(Context<QueryNode> context) {
				QueryNode qn = new QueryNode();
				QueryNode cn = context.getValueStack().peek();
				qn.parent = cn;
				cn.subs.add(sb.toString());
				sb = new StringBuilder();
				cn.subs.add(qn);
				context.getValueStack().push(qn);
				return true;
			}
		});
	}

	Rule closeParen() {
		return Sequence(Ch(')'), new Action<QueryNode>() {

			@Override
			public boolean run(Context<QueryNode> context) {
				String txt = sb.toString();
				QueryNode leavingNode = context.getValueStack().pop();
				if (!txt.isEmpty()) {
					leavingNode.subs.add(txt);
				}
				sb = new StringBuilder();
				return false;
			}
		});
	}

	Rule whitespace() {
		return OneOrMore(AnyOf(WHITESPACE));
	}

	Rule word() {
		return OneOrMore(NoneOf(WHITESPACE + "()'\""));
	}

	Rule text() {
		return Sequence(OneOrMore(FirstOf(word(), whitespace())),
				new Action<QueryNode>() {

					@Override
					public boolean run(Context<QueryNode> context) {
						sb.append(context.getMatch());
						return true;
					}

				});
	}

	Rule root() {
		return Sequence(new Action<QueryNode>() {

			@Override
			public boolean run(Context<QueryNode> context) {
				if (context.getValueStack().isEmpty()) {
					context.getValueStack().push(new QueryNode());
				}
				return true;
			}
		}, OneOrMore(FirstOf(quotedText(), openParen(), closeParen(), text())),
				new Action<QueryNode>() {

					@Override
					public boolean run(Context<QueryNode> context) {
						String txt = sb.toString();
						if (!txt.isEmpty()) {
							context.getValueStack().peek().subs.add(txt);
						}
						return true;
					}
				});
	}
}
