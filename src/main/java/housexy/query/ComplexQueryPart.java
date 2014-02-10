package housexy.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;

public class ComplexQueryPart implements QueryPart {
	private List<? extends QueryPart> subParts = new ArrayList<QueryPart>();
	private boolean and;
	
	public ComplexQueryPart(boolean and,List<? extends QueryPart> subParts) {
		this.and = and;
		this.subParts = subParts;
	}
	
	@Override
	public boolean passes(Object house) {
		int passCount = 0;
		int failCount = 0;
		for (QueryPart sp : subParts) {
			if (sp.passes(house)) {
				passCount++;
			} else {
				failCount++;
			}
		}
		if (and) {
			return failCount==0;
		} else { // or operation
			return passCount>0;
		}
	}
	public String toString() {
		List<String> ss = new ArrayList<String>();
		for (QueryPart qp : subParts) {
			ss.add(qp.toString());
		}
		return "("+StringUtils.join(ss,and?" and ":" or ")+")";
	}
	public Filter toFilter(byte[] family) {
		FilterList f = new FilterList();
		for (QueryPart p : this.subParts) {
			f.addFilter(p.toFilter(family));
		}
		return f;
	}
}
