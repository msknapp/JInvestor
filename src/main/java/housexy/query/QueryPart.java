package housexy.query;

import org.apache.hadoop.hbase.filter.Filter;


public interface QueryPart {
	boolean passes(Object house);
	Filter toFilter(byte[] family);
}
