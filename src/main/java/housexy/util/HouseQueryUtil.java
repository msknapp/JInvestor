package housexy.util;

import housexy.House;
import housexy.query.QueryNode;
import housexy.query.QueryPart;

public final class HouseQueryUtil {
	private HouseQueryUtil(){}
	
	public static boolean matches(House house,String query) {
		QueryPart qp = QueryNode.parseNode(query).toQueryPart();
		return qp.passes(house);
	}
	
}