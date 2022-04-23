package net.sytes.botg.pojoxml;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sytes.botg.reflection.JFlex;

public class PoJoXML {

	private static final String TO = "<";	// opening tag identifier at the beginning of tag
	private static final String TC = ">";	// closing tag identifier
	private static final String TOE = "</";	// opening tag identifier at end of tag
	private static final String TAB = "\t";	// TABULATOR string
	private static final String NL = "\n";	// new line  string
	private static final String CO = " class=\"";	// open class attribute
	private static final String CC = "\"";	// close class attribute
	
	private static final Class<?>[] CLASSES_TO_IGNORE = {Logger.class, Class.class};
	
	private static final Logger logger = LoggerFactory.getLogger(PoJoXML.class);
	
	/**
	 * returns the Object as XML schema
	 * @param object
	 * @return
	 */
	public static String toXML(Object object) {
		
		StringBuilder sb = new StringBuilder();
		
		toXMLRecursive(sb, object, null, 0, new HashMap<Object, Object>());
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param sb
	 * @param object
	 * @param level
	 * @param duplicateMap
	 */
	private static void toXMLRecursive(StringBuilder sb, Object object, String objName, int level, Map<Object, Object> duplicateMap) {
		if (object == null) {
			return;
		}
		// check if object has already been mapped to xml
		if (duplicateMap.containsKey(object)) {
			return;
		} else {
			duplicateMap.put(object, object);
		}
		
		// create new tag with object name
		Class<?> objClass = object.getClass();
		if (isIgnoreClass(objClass)) {
			return;
		}
		if (objName == null) {
			objName = objClass.getSimpleName().toLowerCase();
		}
		sb.append(PoJoUtil.repeat(level, TAB)).append(TO).append(objName).append(CO).append(objClass.getName()).append(CC).append(TC).append(NL);
		
		// get all object fields
		List<Field> objFields = JFlex.getFields(objClass);
		
		// iterate through object fields and turn primitives and sub objects to xml
		for (Field objField : objFields) {
			// check if field should be ignored
			if (PoJoUtil.isIgnorePoJoXMLField(objField)) {
				continue;
			}
			if (isIgnoreClass(objField.getType())) {
				continue;
			}
			String fieldName = objField.getName();
			Object fieldValue = null;
			// get class of object				
			Class<?> fieldValueClass = null;
			try {
				fieldValue = JFlex.getFieldValue(object, fieldName);
				if (fieldValue == null) {
					continue;
				}
				fieldValueClass = fieldValue.getClass();
				if (isIgnoreClass(fieldValueClass)) {
					continue;
				}
			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
				logger.error("Could not parse object property with field '" + fieldName + "'", e);
			}
			if (JFlex.isPrimitive(objField.getType(), true) || objField.getType().equals(String.class) || objField.getType().equals(Object.class)) {
				sb.append(PoJoUtil.repeat(level + 1, TAB)).append(TO).append(fieldName).append(TC).append(fieldValue).append(TOE).append(fieldName).append(TC).append(NL);
			} else if (fieldValueClass.isEnum()) {
				sb.append(PoJoUtil.repeat(level + 1, TAB)).append(TO).append(fieldName).append(TC).append(fieldValue.toString()).append(TOE).append(fieldName).append(TC).append(NL);
			} else {
				// extract generic in case of array, map or collection
				Type type = objField.getGenericType();
				String typeClass = type.getTypeName();
				if (JFlex.isMap(fieldValueClass) || fieldValueClass.isArray() || JFlex.isCollection(fieldValueClass)) {
					
					// convert to object array
	 				Object[] arrayObjs = null;
					if (JFlex.isMap(fieldValue.getClass())) {
						// TODO map to string for exporting/importing logic required
						typeClass = type.getTypeName();
						// extract map value class
						typeClass = typeClass.substring(typeClass.indexOf(", ") + 1, typeClass.indexOf(">")).trim();
						Class<?> typeClazz = JFlex.parseString2Class(typeClass);
						if (typeClazz == null) {
							continue;
						}
						typeClass = typeClazz.getSimpleName();
						Map<?, Object> map = (Map<?, Object>) fieldValue;
						arrayObjs = map.values().toArray();
					} else if (JFlex.isCollection(fieldValue.getClass())) {
						// TODO collection to string for exporting/importing logic required
						Class<?> typeClazz = JFlex.parseString2Class(typeClass);
						if (typeClazz == null) {
							continue;
						}
						typeClass = typeClazz.getClass().getSimpleName();
						Collection<Object> objCollection = (Collection<Object>) fieldValue;
						arrayObjs = objCollection.toArray();
					} else {
						Class<?> typeClazz = JFlex.parseString2Class(typeClass);
						if (typeClazz == null) {
							continue;
						}
						typeClass = typeClazz.getSimpleName();
						//Collection<Object> objCollection = (Collection<Object>) fieldValue;
						arrayObjs = (Object[]) fieldValue;
					}
					if (arrayObjs == null) {
						continue;
					}
					if (arrayObjs.length == 0) {
						continue;
					}
					// get first object to retrieve class
					Object firstObj = arrayObjs[0];
					if (firstObj == null) {
						continue;
					}
					Class<?> firstObjClass = firstObj.getClass();
					if (JFlex.isPrimitive(firstObjClass, true) || firstObjClass.equals(String.class)) {
						// print primitives as comma separated string
						sb.append(PoJoUtil.repeat(level + 1, TAB)).append(TO).append(fieldName).append(CO).append(typeClass).append(CC).append(TC).append(NL);
						String arrayStr = Arrays.toString(arrayObjs);
						sb.append(PoJoUtil.repeat(level + 2, TAB)).append(arrayStr.subSequence(1, arrayStr.length() - 1)).append(NL);
						sb.append(PoJoUtil.repeat(level + 1, TAB)).append(TOE).append(fieldName).append(TC).append(NL);
					} else {					
						// create tag for more than one object of same type
						sb.append(PoJoUtil.repeat(level + 1, TAB)).append(TO).append(fieldName).append(CO).append(typeClass).append(CC).append(TC).append(NL);
										
						// iterate through array
						for (Object arrayObj : arrayObjs) {
							toXMLRecursive(sb, arrayObj, fieldName, level + 2, duplicateMap);
						}
						
						// close tag for more than one object of same type
						sb.append(PoJoUtil.repeat(level + 1, TAB)).append(TOE).append(fieldName).append(TC).append(NL);
					}
				} else {
					toXMLRecursive(sb, fieldValue, fieldName, level + 1, duplicateMap);
				}
			}
		}
		
		// close tag
		sb.append(PoJoUtil.repeat(level, TAB)).append(TOE).append(objName).append(TC).append(NL);
	}
	
	private static boolean isIgnoreClass(Class<?> clazz) {
		for (Class<?> c : CLASSES_TO_IGNORE) {
			if (c.equals(clazz)) {
				return true;
			}
		}
		return false;
	}
}
