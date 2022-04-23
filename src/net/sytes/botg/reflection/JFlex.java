package net.sytes.botg.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sytes.botg.datatypes.DataType;

public class JFlex {

	//private static final Logger logger = LoggerFactory.getLogger(ClassParser.class);
	
	private static final Set<Class<?>> PRIMITIVE_WRAPPERS = new HashSet<Class<?>> (Arrays.asList(
															Boolean.class,
															Byte.class,
															Short.class,
															Integer.class,
															Long.class,
															Float.class,
															Double.class,
															Character.class,
															String.class
														));
	
	private static final Logger logger = LoggerFactory.getLogger(JFlex.class);
	
	/**
	 * configure single property 
	 * @param object
	 * @param propertyName
	 * @param propertyValue
	 * @throws JFlexException
	 */
	public static void configure(Object object, String propertyName, String propertyValue) throws JFlexException {
		configure(object, propertyName, propertyValue, null);
	}
	
	/**
	 * configure single property with enum options
	 * @param object
	 * @param propertyName
	 * @param propertyValue
	 * @param enumClasses
	 * @throws JFlexException
	 */
	public static void configure(Object object, String propertyName, String propertyValue, Class<?>[] enumClasses) throws JFlexException {
		Map<String, String> configMap = new HashMap<String, String>();
		configMap.put(propertyName, propertyValue);
		configure(object, configMap, enumClasses);
	}
	
	/**
	 * configure
	 * @param object
	 * @param configMap
	 * @throws JFlexException
	 */
	public static void configure(Object object, Map<String, String> configMap) throws JFlexException {
		configure(object, configMap, null);
	}
	
	/**
	 * configures the object by configMap up to a parent class level of levelOfRecursion
	 * @param object
	 * @param configMap
	 * @param enumClasses
	 * @throws JFlexException
	 */
	public static void configure(Object object, Map<String, String> configMap, Class<?>[] enumClasses) throws JFlexException {
		// go through properties of object through reflection and set them using properties stored within configMap
		List<Field> fields = JFlex.getFields(object.getClass());
		for (Field field : fields) {
			String propertyValue = configMap.get(field.getName());
			if (propertyValue != null) {
				setObjectProperty(object, field, propertyValue, enumClasses);
			}
		}		
	}
	
	public static void configure2(Object object, Map<String, Object> configMap, Class<?>[] enumClasses) throws JFlexException {
		// go through properties of object through reflection and set them using properties stored within configMap
		List<Field> fields = JFlex.getFields(object.getClass());
		for (Field field : fields) {
			Object propertyValue = configMap.get(field.getName());
			if (propertyValue != null) {
				setObjectProperty2(object, field, propertyValue, enumClasses);
			}
		}	
	}
	

	
	/**
	 * set a property of gatewayObject by fieldname=property with propertyValue 
	 * @param gatewayObject
	 * @param property
	 * @param propertyValue
	 * @param enumClasses
	 * @return
	 * @throws MyGatewayBuilderException
	 */
	public static boolean setObjectProperty(Object object, Field field,  String propertyValue, Class<?>[] enumClasses) throws JFlexException {
		try {	
			JFlex.setFieldValue(object, field.getName(), castPropertyValue(field.getType().getName(), propertyValue, enumClasses));
			return true;
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
			// TODO Auto-generated catch block
        	logger.error(e.getMessage(), e);
			throw new JFlexException("Error while configuring Object " + object.getClass().getName() + " [" + field.getName() + "=" + propertyValue + "] using Reflection");
		}
	}
	
	public static boolean setObjectProperty2(Object object, Field field,  Object propertyValue, Class<?>[] enumClasses) throws JFlexException {
		try {	
			JFlex.setFieldValue(object, field.getName(), castPropertyValue2(propertyValue, enumClasses));
			return true;
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
			// TODO Auto-generated catch block
        	logger.error(e.getMessage(), e);
			throw new JFlexException("Error while configuring Object " + object.getClass().getName() + " [" + field.getName() + "=" + propertyValue + "] using Reflection");
		}
	}
	
	/**
	 * retrieves the object's property of Field fieldName
	 * @param object
	 * @param fieldName
	 * @return
	 * @throws JFlexException
	 */
	public static Object getObjectProperty(Object object, String fieldName) throws JFlexException {
		try {
			return JFlex.getFieldValue(object, fieldName);		
		} catch (SecurityException e) {
			throw new JFlexException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new JFlexException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new JFlexException(e.getMessage());
		} catch (NoSuchFieldException e) {
			throw new JFlexException(e.getMessage());
		}		
	}
	
	
	/**
	 * cast the property value defined by dataType
	 * TODO no support of char yet and the array form of primitives
	 * @param <T>
	 * @param dataType
	 * @param propertyValue
	 * @param enumClasses
	 * @return
	 * @throws JFlexException
	 */
	public static Object castPropertyValue(String dataType, String propertyValue, Class<?>[] enumClasses) throws JFlexException {
		Class<?> dataTypeClass = null;
		try {
			dataTypeClass = JFlex.parseString2Class(dataType);
			if (dataTypeClass == null) {
				dataTypeClass = Class.forName(dataType);
			}
		} catch (ClassNotFoundException e) {
			throw new JFlexException("unkown class " + dataType + ", cannot instantiate object of this class");
		}
		if (JFlex.isPrimitive(dataTypeClass, true)) {
			return DataType.convertToDataType(DataType.convertToType(dataType), propertyValue);
			/*
			switch(dataType.toLowerCase()) {
				case "int":
					if (propertyValue == "") {
						return 0;
					} else {
						return Integer.parseInt(propertyValue);
					}
					
				case "boolean":
					if (propertyValue == "") {
						return false;
					} else {
						return Boolean.parseBoolean(propertyValue);
					}
					
				case "double":
					if (propertyValue == "") {
						return 0.0;
					} else {
						return Double.parseDouble(propertyValue);
					}
					
				case "long":
					if (propertyValue == "") {
						return 0;
					} else {
						return Long.parseLong(propertyValue);
					}
				
				case "byte":
					if (propertyValue == "") {
						return 0;
					} else {
						return Byte.parseByte(propertyValue);
					}
					
				case "short":
					if (propertyValue == "") {
						return 0;
					} else {
						return Short.parseShort(propertyValue);
					}
					
				case "float":
					if (propertyValue == "") {
						return 0.0;
					} else {
						return Float.parseFloat(propertyValue);
					}
					
				case "java.lang.string":
					return propertyValue;
									
				default:
					throw new JFlexException("Unkown datatype " + dataType + ", cannot cast property value");			
			}
			*/
		} else if (dataTypeClass.isEnum()){
			if (enumClasses != null) {
				if (enumClasses.length > 0) {
					for (Class enumClass : enumClasses) {
						try {
							Object enumValue = Enum.valueOf(enumClass, propertyValue);
							return enumValue;
						} catch (IllegalArgumentException e) {
							logger.debug(enumClass.getName() + " does not contain constant with name = " + propertyValue, e);
						}
					}
					return null;
				} else {
					throw new JFlexException("Could not parse Enum [" + dataType + "], because array of enumClasses to parse from was empty");
				}
			} else {
				throw new JFlexException("Could not parse Enum [" + dataType + "], because no enumClasses to parse from were supplied");
			}
		} else if (dataTypeClass == String.class) {
			return propertyValue;
		} else {
			throw new JFlexException("only primitives and enumerations can be parsed from string. Cannot instantiate object of class " + dataType);
		}
	}
	
	public static Object castPropertyValue2(Object propertyValue, Class<?>[] enumClasses) throws JFlexException {
		Class<?> dataTypeClass = null;
		try {
			dataTypeClass = JFlex.parseString2Class(propertyValue.getClass().getName());
			if (dataTypeClass == null) {
				dataTypeClass = Class.forName(propertyValue.getClass().getName());
			}
		} catch (ClassNotFoundException e) {
			throw new JFlexException("unkown class " + propertyValue.getClass().getName() + ", cannot instantiate object of this class");
		}
		if (dataTypeClass == String.class) {
			return propertyValue;
		}else if (JFlex.isPrimitive(dataTypeClass, true)) {
			return DataType.convertToDataType(DataType.convertToType(dataTypeClass.getSimpleName()), propertyValue.toString());
		} else if (dataTypeClass.isEnum()){
			if (enumClasses != null) {
				if (enumClasses.length > 0) {
					for (Class enumClass : enumClasses) {
						try {
							Object enumValue = Enum.valueOf(enumClass, (String) propertyValue);
							return enumValue;
						} catch (IllegalArgumentException e) {
							logger.debug(enumClass.getName() + " does not contain constant with name = " + propertyValue, e);
						}
					}
					return null;
				} else {
					throw new JFlexException("Could not parse Enum [" + propertyValue.getClass().getName() + "], because array of enumClasses to parse from was empty");
				}
			} else {
				throw new JFlexException("Could not parse Enum [" + propertyValue.getClass().getName() + "], because no enumClasses to parse from were supplied");
			}
		} else {
			throw new JFlexException("only primitives and enumerations can be parsed from string. Cannot instantiate object of class " + propertyValue.getClass().getName());
		}
	}
	
	public static Object castToTarget(Object obj, Class<?> targetType) {
		Class<?> sourceType = obj.getClass();
		if (targetType.equals(String.class)) {
			return obj.toString();
		} else if (sourceType.equals(String.class)) {
			return DataType.convertToDataType(DataType.convertToType(targetType.getSimpleName()), obj.toString());
		} else if (JFlex.isPrimitive(sourceType, true)) {
			return DataType.convertToDataType(obj, DataType.convertClassToType(sourceType), DataType.convertClassToType(targetType));
		} else {
			return null;
		}
	}
	
	public static Object getFieldValue(Object object, String fieldName) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getField(object.getClass(), fieldName);
		field.setAccessible(true);
		return field.get(object);
	}
	
	public static void setFieldValue(Object object, String fieldName, Object fieldValue) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getField(object.getClass(), fieldName);
		field.setAccessible(true);
		
		// check if Double have to be converted to long or int
		if (field.getType().equals(long.class)) {
			long l = ((Double) fieldValue).longValue();
			field.set(object, l);
			return;
		}
		if (field.getType().equals(int.class)) {
			int i = ((Double) fieldValue).intValue();
			field.set(object, i);
			return;
		}
		
		field.set(object, fieldValue);
	}
	
	/**
	 * retrieves a Field in clazz specified by fieldName
	 * the method also considers the class' subclasses recursively
	 * @param clazz
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 */
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
	
	/**
	 * retrieves a Method based on clazz to search in and the methodName and its input parameter classes
	 * @param clazz
	 * @param methodName
	 * @param inputClasses
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public static Method getMethod(Class<?> clazz, String methodName, Class<?> ... inputClasses) throws SecurityException, NoSuchMethodException {
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(methodName, inputClasses);
		} catch (NoSuchMethodException e) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null) {
				method = getMethod(superClass, methodName, inputClasses);
			} else {
				throw new NoSuchMethodException("Method '" + methodName + "' could not be found!");
			}
		}
		return method;
	}
	
	public static List<Field> getFields(Class<?> clazz) {
		List<Field> fieldList = new ArrayList<Field>();
		Field[] fields = clazz.getDeclaredFields();
		fieldList.addAll(Arrays.asList(fields));
		if (clazz.getSuperclass() != Object.class & clazz.getSuperclass() != null) {
			fieldList.addAll(getFields(clazz.getSuperclass()));
		}
		return fieldList;
	}
	
	/**
	 * returns a primitive or Object class based on {@code classStr}
	 * @param classStr
	 * @return
	 */
	public static Class<?> parseString2Class(String classStr){
		switch (classStr.toLowerCase()) {
			case "boolean":
				return boolean.class;
			case "boolean[]":
				return boolean[].class;
			case "java.lang.boolean":
				return boolean.class;
			case "byte":
				return byte.class;
			case "int8":
				return byte.class;
			case "byte[]":
				return byte[].class;
			case "short":
				return short.class;
			case "int16":
				return short.class;
			case "short[]":
				return short[].class;
			case "float":
				return float.class;
			case "float[]":
				return float[].class;
			case "double":
				return double.class;
			case "double[]":
				return Double[].class;
			case "int":
				return int.class;
			case "int32":
				return int.class;
			case "int[]":
				return int[].class;
			case "integer":
				return int.class;
			case "long":
				return long.class;
			case "long[]":
				return long[].class;
			case "string":
				return String.class;
			case "string[]":
				return String[].class;
			case "java.lang.string[]":
				return String[].class;
			case "java.lang.string":
				return String.class;
			case "object":
				return Object.class;
			case "object[]":
				return Object[].class;
			default: 
				try {
					return Class.forName(classStr);
				} catch (ClassNotFoundException e) {
					logger.error("Could not create object of class '" + classStr + "'", e);
					return null;
				}
		}
	}
	
	/**
	 * check if clazz or superclasses implement interfaceClazz
	 * @param clazz
	 * @param interfaceClazz
	 * @return true/false
	 */
	public static boolean implementsInterface(Class<?> clazz, Class<?> interfaceClazz) {
		for (Class<?> c : clazz.getInterfaces()) {
			if (c.equals(interfaceClazz)) {
				return true;
			}
		}
		Class<?> sc = clazz.getSuperclass();
		if (sc != null) {
			return implementsInterface(sc, interfaceClazz);
		}
		return false;
	}
	
	/**
	 * recursive check if Class clazz has Class superClazz as superclass somewhere along its class hierarchy
	 * @param clazz
	 * @param superClazz
	 * @return
	 */
	public static boolean hasSuperClass(Class<?> clazz, Class<?> superClazz) {
		if (!clazz.equals(superClazz)) {
			if (clazz.getSuperclass() != null) {
				if (clazz.getSuperclass().equals(superClazz)) {
					return true;
				} else {
					return hasSuperClass(clazz.getSuperclass(), superClazz);
				}
			}
			return false;
		}
		return false;
	}
	
	public static boolean isPrimitive(Class<?> clazz, boolean includePrimitiveWrapper) {
		if (clazz.isPrimitive()) {
			return true;
		}
		if (isPrimitiveWrapper(clazz)) {
			return true;
		}
		return false;
	}
	
	public static boolean isPrimitiveWrapper(Class<?> clazz) {
        return PRIMITIVE_WRAPPERS.contains(clazz);
    }
	
	public static boolean isCollection(Class<?> clazz) {
		return JFlex.hasSuperClass(clazz, Collection.class) || JFlex.implementsInterface(clazz, Collection.class);
	}
	
	public static boolean isMap(Class<?> clazz) {
		return JFlex.implementsInterface(clazz, Map.class);
	}
	
	/**
	 * creates an object by using the full qualified class name as string
	 * @param classStr
	 * @return
	 * @throws JFlexException 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static Object createObject(String classStr) throws JFlexException {
		if (classStr != null) {
			try {
				return Class.forName(classStr).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				throw new JFlexException("Could not create object for " + classStr + "\n" + e.getMessage());
			}
		} else {
			throw new JFlexException("No string was specified");
		}
	}
	
	private boolean isFieldArray(Object object, String fieldName) throws JFlexException {
		try {
			return object.getClass().getDeclaredField(fieldName).getType().isArray();
		} catch (NoSuchFieldException e) {
			throw new JFlexException(e.getMessage());
		} catch (SecurityException e) {
			throw new JFlexException(e.getMessage());
		}
	}
	
	private boolean isFieldList() {
		// TODO Not implemented
		return false;
	}	
	
	public static class JFlexException extends Exception {
		
		private static final long serialVersionUID = 4212451547162219357L;

		public JFlexException(String message) {
			super(message);
		}
		
	}	
}
