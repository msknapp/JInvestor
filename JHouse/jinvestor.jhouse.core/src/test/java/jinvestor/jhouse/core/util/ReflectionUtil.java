package jinvestor.jhouse.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.reflections.ReflectionUtils;

import com.google.common.base.Predicate;

public final class ReflectionUtil {
	private ReflectionUtil() {}
	
	public static Set<String> getFieldNames(Class<?> clazz) {
		return getFieldNamesWithout(clazz,ReflectionUtils.withModifier(Modifier.STATIC));
	}
	
	public static Set<String> getFieldNames(Class<?> clazz,Predicate<? super Field> ... predicates) {
		Set<Field> fields = ReflectionUtils.getFields(clazz,predicates);
		Set<String> names = new HashSet<String>();
		for (Field f : fields) {
			names.add(f.getName());
		}
		return names;
	}
	
	public static Set<String> getFieldNamesWithout(Class<?> clazz,Predicate<? super Field> ... removePredicates) {
		Set<Field> fields = ReflectionUtils.getFields(clazz);
		fields.removeAll(ReflectionUtils.getAllFields(clazz, removePredicates));
		Set<String> names = new HashSet<String>();
		for (Field f : fields) {
			names.add(f.getName());
		}
		return names;
	}
	
	public static boolean set(Object target,String fieldName,Object fieldValue) {
		Set<Field> fields = ReflectionUtils.getFields(target.getClass(), ReflectionUtils.withName(fieldName));
		if (!fields.isEmpty()) {
			Field field = fields.iterator().next();
			boolean wasAccessible = Modifier.isPublic(field.getModifiers()) || field.isAccessible();
			try {
				if (!wasAccessible) {
					field.setAccessible(true);
				}
				field.set(target, fieldValue);
				return true;
			} catch (Exception e) {
				if (!wasAccessible) {
					field.setAccessible(false);
				}
			}
		}
		return false;
	}
	
	public static Object get(Object target,String fieldName,MutableBoolean worked) {
		Set<Field> fields = ReflectionUtils.getFields(target.getClass(), ReflectionUtils.withName(fieldName));
		Object found = null;
		worked.setValue(Boolean.FALSE);
		if (!fields.isEmpty()) {
			Field field = fields.iterator().next();
			boolean wasAccessible = Modifier.isPublic(field.getModifiers()) || field.isAccessible();
			try {
				if (!wasAccessible) {
					field.setAccessible(true);
				}
				found = field.get(target);
				worked.setValue(Boolean.TRUE);
			} catch (Exception e) {
				if (!wasAccessible) {
					field.setAccessible(false);
				}
			}
		}
		return found;
	}
}
