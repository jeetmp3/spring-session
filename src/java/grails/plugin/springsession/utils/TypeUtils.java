package grails.plugin.springsession.utils;

import groovy.lang.GString;
import org.codehaus.groovy.runtime.GStringImpl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class TypeUtils {

	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

	public static boolean isNullOrWrapperType(Object object) {
		return object == null || isWrapperType(object.getClass());
	}

	public static boolean isWrapperType(Class<?> clazz) {
		return WRAPPER_TYPES.contains(clazz);
	}

	private static Set<Class<?>> getWrapperTypes() {
		Set<Class<?>> ret = new HashSet<Class<?>>();
		ret.add(Boolean.class);
		ret.add(Character.class);
		ret.add(Byte.class);
		ret.add(Short.class);
		ret.add(Integer.class);
		ret.add(Long.class);
		ret.add(Float.class);
		ret.add(Double.class);
		ret.add(BigDecimal.class);
		ret.add(Void.class);
		ret.add(String.class);
		ret.add(GString.class);
		ret.add(GStringImpl.class);
		return ret;
	}

}
