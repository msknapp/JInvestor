package jinvestor.jhouse.core.query;

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

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;

public class SimpleQueryPart implements QueryPart {
	private final String field,operation,value;
	
	private SimpleQueryPart(SimpleQueryPartBuilder builder) {
		this.field = builder.field;
		this.operation = builder.operation;
		this.value = builder.value;
	}
	
	public static final SimpleQueryPartBuilder builder() {
		return new SimpleQueryPartBuilder();
	}
	
	public static final class SimpleQueryPartBuilder implements QueryPartBuilder {
		private String field,operation,value;
		
		public SimpleQueryPartBuilder() {
			
		}
		public SimpleQueryPartBuilder(SimpleQueryPart original) {
			this.field = original.field;
			this.operation = original.operation;
			this.value = original.value;
		}
		public SimpleQueryPartBuilder(SimpleQueryPartBuilder builder) {
			this.field = builder.field;
			this.operation = builder.operation;
			this.value = builder.value;
		}
		public SimpleQueryPartBuilder field(String field) {
			this.field = field;
			return this;
		}
		public SimpleQueryPartBuilder operation(String operation) {
			this.operation = operation;
			return this;
		}
		public SimpleQueryPartBuilder value(String value) {
			this.value = value;
			return this;
		}
		public SimpleQueryPart build() {
			return new SimpleQueryPart(this);
		}
	}
	
	public static final SimpleQueryPart fromQuery(String query) {
		SimpleQueryPartBuilder b = new SimpleQueryPartBuilder();
		String regex = "(\\w+)\\s*(={1,2}|<|>|like)\\s*(.*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(query);
		if (m.matches()) {
			b.field(m.group(1));
			b.operation(m.group(2));
			String value = m.group(3);
			if (value.startsWith("'")) {
				value = value.substring(1);
			}
			if (value.endsWith("'")) {
				value = value.substring(0,value.length()-1);
			}
			b.value(value);
		} else {
			System.out.println("failed to match "+query);
		}
		return b.build();
	}
	
	@SuppressWarnings("unchecked")
	public boolean passes(Object house) {
		try {
			Field f = house.getClass().getDeclaredField(field);
			f.setAccessible(true);
			Object o = f.get(house);
			Object myValue = null;
			if (value!=null && o!=null) {
				myValue = castValue(value,o.getClass());
			}
			if ("=".equals(operation) || "==".equals(operation)) {
				if ("null".equals(value)) {
					return o==null;
				}
				return o.equals(myValue);
			}
			if (o == null) {
				return false;
			}
			if (">".equals(operation)) {
				return ((Comparable)o).compareTo(myValue)>0;
			}else if ("<".equals(operation)) {
				return ((Comparable)o).compareTo(myValue)<0;
			} if (">=".equals(operation)) {
				return ((Comparable)o).compareTo(myValue)>=0;
			}else if ("<=".equals(operation)) {
				return ((Comparable)o).compareTo(myValue)<=0;
			} 
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private Object castValue(String value, Class<? extends Object> clazz) {
		if (Byte.class==clazz) {
			return Byte.parseByte(value);
		} else if (Short.class==clazz) {
			return Short.parseShort(value);
		} else if (Long.class==clazz) {
			return Long.parseLong(value);
		} else if (Integer.class==clazz) {
			return Integer.parseInt(value);
		} else if (Float.class==clazz) {
			return Float.parseFloat(value);
		} else if (Double.class==clazz) {
			return Double.parseDouble(value);
		}
		return value.toString();
	}

	public String toString() {
		return String.format("%s %s '%s'",field,operation,value.replace("'","\\'"));
	}
	
	public Filter toFilter(byte[] family) {
		return new SingleColumnValueFilter(family,field.getBytes(),determineCompareOp(),value.getBytes());
	}

	private CompareOp determineCompareOp() {
		if ("=".equals(this.operation) || "==".equals(this.operation)) {
			return CompareOp.EQUAL;
		}
		if ("!=".equals(this.operation)) {
			return CompareOp.NOT_EQUAL;
		}
		if (">".equals(this.operation)) {
			return CompareOp.GREATER;
		}
		if ("<".equals(this.operation)) {
			return CompareOp.LESS;
		}
		if (">=".equals(this.operation)) {
			return CompareOp.GREATER_OR_EQUAL;
		}
		if ("<=".equals(this.operation)) {
			return CompareOp.LESS_OR_EQUAL;
		}
		return CompareOp.NO_OP;
	}
}