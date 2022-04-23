package net.sytes.botg.configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sytes.botg.reflection.JFlex;
import net.sytes.botg.reflection.JFlex.JFlexException;

public class ConfigOptionParser {

	/**
	 * 
	 * @param object
	 * @return
	 * @throws ConfigOptionException
	 */
	public static String getConfigString(Object object) throws ConfigOptionException{
		Map<String, Object> configOptionMap = getConfigOptionMap(object);
		return convertMapToNameValuePairString(configOptionMap, ";");
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 * @throws ConfigOptionException
	 */
	public static Map<String, Object> getConfigOptionMap(Object object) throws ConfigOptionException {
		Map<String, Object> configOptionMap = new LinkedHashMap<String, Object>();
		List<Field> configFields = getConfigOptions(object.getClass());
		for (Field field : configFields) {
			try {
				Object fieldValue = JFlex.getObjectProperty(object, field.getName());
				configOptionMap.put(field.getName(), fieldValue);
			} catch (JFlexException e) {
				throw new ConfigOptionException(e.getMessage());
			}
		}
		return configOptionMap;
	}
	
	public static Map<String, String> getConfigOptionTypeMap(Object object) throws ConfigOptionException {
		Map<String, String> configOptionMap = new LinkedHashMap<String, String>();
		List<Field> configFields = getConfigOptions(object.getClass());
		for (Field field : configFields) {
			String c = field.getType().getSimpleName();
			configOptionMap.put(field.getName(), c);
		}
		return configOptionMap;
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 */
	private static List<Field> getConfigOptions(Class<?> clazz) throws ConfigOptionException {
		List<Field> configFields = new ArrayList<Field>();
		for(Field field : clazz.getDeclaredFields()){
			if (isConfigOption(field)) {
				configFields.add(field);
			}
		}
		if (clazz.getSuperclass() != null) {
			List<Field> moreConfigFields = getConfigOptions(clazz.getSuperclass());
			configFields.addAll(moreConfigFields);
		}
		return configFields;
	}
	
	/**
	 * 
	 * @param field
	 * @return
	 */
	private static boolean isConfigOption(Field field) {
		ConfigOption annot = field.getAnnotation(ConfigOption.class);
		if(annot == null) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 
	 * @param map
	 * @param delimiter
	 * @return
	 */
	private static String convertMapToNameValuePairString(Map<String, Object> map, String delimiter) {
		String s = "";
		s = map.toString();
		s = s.substring(1, s.length() - 1);
		if (delimiter != ",") {
			s = s.replace(", ", delimiter);
		}
		return s;
	}
	
	/**
	 * 
	 * @author hillenbrand
	 *
	 */
	public static class ConfigOptionException extends Exception {

		private static final long serialVersionUID = 5107238582275791317L;
		
		public ConfigOptionException(String message) {
			super(message);
		}
		
	}
	
}
