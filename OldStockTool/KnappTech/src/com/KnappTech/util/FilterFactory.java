package com.KnappTech.util;

public class FilterFactory {
	
	public static Filter<Object> acceptAllFilter() {
		return new Filter<Object>() {
			@Override
			public boolean include(Object object) {
				return true;
			}
		};
	}
	
	public static Filter<Object> rejectClassFilter(Class<?> clazz) {
		return new RejectClassFilter(clazz);
	}
	
	private static class RejectClassFilter implements Filter<Object> {
		private final Class<?> clazz;
		public RejectClassFilter(Class<?> clazz) {
			this.clazz = clazz;
		}
		@Override
		public boolean include(Object object) {
			return (!(clazz.isInstance(object.getClass())));
		}
	}
}