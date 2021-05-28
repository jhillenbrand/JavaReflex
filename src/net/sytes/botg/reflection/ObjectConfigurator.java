package net.sytes.botg.reflection;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectConfigurator {
	
	private static final Logger logger = LoggerFactory.getLogger(ObjectConfigurator.class);
	
	public static void configure(Object object, String propertyName, String propertyValue) throws ConfigException {
		Map<String, String> configMap = new HashMap<String, String>();
		configMap.put(propertyName, propertyValue);
		configure(object, configMap);
	}
	
	/**
	 * configures the object by configMap up to a parent class level of levelOfRecursion
	 * @param object
	 * @param configMap
	 * @throws ConfigException
	 */
	public static void configure(Object object, Map<String, String> configMap) throws ConfigException {
		// go through properties of object through reflection and set them using properties stored within configMap
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			String propertyValue = configMap.get(field.getName());
			if (propertyValue != null) {
				setObjectProperty(object, field, propertyValue);
			}
		}		
	}
	
	/**
	 * set a property of gatewayObject by fieldname=property with propertyValue 
	 * @param gatewayObject
	 * @param property
	 * @param propertyValue
	 * @return
	 * @throws MyGatewayBuilderException
	 */
	public static boolean setObjectProperty(Object object, Field field,  String propertyValue) throws ConfigException {
		try {	
			ClassParser.setFieldValue(object, field.getName(), castPropertyValue(field.getType().getName(), propertyValue));
			return true;
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
			// TODO Auto-generated catch block
        	logger.error(e.getMessage(), e);
			throw new ConfigException("Error while configuring Object " + object.getClass().getName() + " [" + field.getName() + "=" + propertyValue + "] using Reflection");
		}
	}
	
	/**
	 * retrieves the object's property of Field fieldName
	 * @param object
	 * @param fieldName
	 * @return
	 * @throws ConfigException
	 */
	public static Object getObjectProperty(Object object, String fieldName) throws ConfigException {
		try {
			return ClassParser.getFieldValue(object, fieldName);		
		} catch (SecurityException e) {
			throw new ConfigException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new ConfigException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ConfigException(e.getMessage());
		} catch (NoSuchFieldException e) {
			throw new ConfigException(e.getMessage());
		}		
	}
	
	
	/**
	 * cast the property value defined by dataType
	 * TODO no support of char yet and the array form of primitives
	 * @param <T>
	 * @param dataType
	 * @param propertyValue
	 * @return
	 * @throws ConfigException
	 */
	public static Object castPropertyValue(String dataType, String propertyValue) throws ConfigException {
		Class<?> dataTypeClass = null;
		try {
			dataTypeClass = Class.forName(dataType);
		} catch (ClassNotFoundException e) {
			throw new ConfigException("unkown class " + dataType + ", cannot instantiate object of this class");
		}
		if (ClassParser.isPrimitive(dataTypeClass, true)) {
			switch(dataType) {
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
					
				case "java.lang.String":
					return propertyValue;
					
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
									
				default:
					throw new ConfigException("Unkown datatype " + dataType + ", cannot cast property value");			
			}
		} else if (dataTypeClass.isEnum()){
			Object dataTypeObj = createObject(dataType);
			Enum<?> dataTypeEnum = (Enum<?>) dataTypeObj;		
			return Enum.valueOf(dataTypeEnum.getClass(), propertyValue);
			//return dataTypeEnum.valueOf(dataTypeObj.getClass(), propertyValue);
		} else {
			throw new ConfigException("only primitives and enumerations can be parsed from string. Cannot instantiate object of class " + dataType);
		}
	}
	
	public static Object createObject(String classStr) throws ConfigException {
		try {
			return ClassParser.createObject(classStr);
		} catch (InstantiationException e) {
			throw new ConfigException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ConfigException(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new ConfigException(e.getMessage());
		}
	}

	private boolean isFieldArray(Object object, String fieldName) throws ConfigException {
		try {
			return object.getClass().getDeclaredField(fieldName).getType().isArray();
		} catch (NoSuchFieldException e) {
			throw new ConfigException(e.getMessage());
		} catch (SecurityException e) {
			throw new ConfigException(e.getMessage());
		}
	}
	
	private boolean isFieldList() {
		return false;
	}
	
	public static class ConfigException extends Exception {
		
		private static final long serialVersionUID = 4212451547162219357L;

		public ConfigException(String message) {
			super(message);
		}
		
	}
	
}
