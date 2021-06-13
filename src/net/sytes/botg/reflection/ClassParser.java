package net.sytes.botg.reflection;

import java.lang.reflect.Field;

import net.sytes.botg.reflection.ObjectConfigurator.ConfigException;

public class ClassParser {

	//private static final Logger logger = LoggerFactory.getLogger(ClassParser.class);
	
	public static Object getFieldValue(Object object, String fieldName) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getField(object.getClass(), fieldName);
		field.setAccessible(true);
		return field.get(object);
	}
	
	public static void setFieldValue(Object object, String fieldName, Object fieldValue) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getField(object.getClass(), fieldName);
		field.setAccessible(true);
		field.set(object, fieldValue);
	}
	
	public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass == null) {
				throw e;
			} else {
				return getField(superClass, fieldName);
			}
		}
	}
	
	public static Class<?> parseStringClass(String classStr){
		switch (classStr.toLowerCase()) {
			case "boolean":
				return boolean.class;
			case "byte":
				return byte[].class;
			case "byte[]":
				return byte[].class;
			case "double":
				return double.class;
			case "double[]":
				return Double[].class;
			case "int":
				return int.class;
			case "integer":
				return Integer.class;
			case "long":
				return long.class;
			case "string":
				return String.class;
			case "java.lang.string":
				return String.class;
			case "object":
				return Object.class;
			default: 
				return null;
		}
	}
	
	/**
	 * check if clazz or superclasses implement interfaceClazz
	 * @param clazz
	 * @param interfaceClazz
	 * @return true/false
	 */
	public static boolean hasInterface(Class<?> clazz, Class<?> interfaceClazz) {
		boolean interfaceFound = false;
		for (Class<?> c : clazz.getInterfaces()) {
			if (c.equals(interfaceClazz)) {
				interfaceFound = true;
			}
		}
		Class<?> sc = clazz.getSuperclass();
		if (sc != null) {
			return hasInterface(sc, interfaceClazz);
		}
		return interfaceFound;
	}
	
	public static boolean isPrimitive(Class<?> clazz, boolean includePrimitiveWrapper) {
		if (clazz.isPrimitive()) {
			return true;
		}
		if (includePrimitiveWrapper) {
			if (clazz.equals(String.class)) {
				return true;
			}
			if (clazz.equals(Integer.class)) {
				return true;
			}
			if (clazz.equals(Object.class)) {
				return true;
			}
			if (clazz.equals(Double.class)) {
				return true;
			}
			if (clazz.equals(Boolean.class)) {
				return true;
			}
			if (clazz.equals(Float.class)) {
				return true;
			}
		}
		return false;
	}

	public static Object createObject(String classStr) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return Class.forName(classStr).newInstance();
	}
}
