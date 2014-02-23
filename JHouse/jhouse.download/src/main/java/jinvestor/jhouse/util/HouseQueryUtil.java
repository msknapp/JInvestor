package jinvestor.jhouse.util;

import jinvestor.jhouse.House;
import jinvestor.jhouse.query.QueryNode;
import jinvestor.jhouse.query.QueryPart;

public final class HouseQueryUtil {
	private HouseQueryUtil(){}
	
	public static boolean matches(House house,String query) {
		QueryPart qp = QueryNode.parseNode(query).toQueryPart();
		return qp.passes(house);
	}
	
}