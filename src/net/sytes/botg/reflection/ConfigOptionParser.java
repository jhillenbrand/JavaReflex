package net.sytes.botg.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sytes.botg.reflection.ObjectConfigurator.ConfigException;

public class ConfigOptionParser {

	/**
	 * 
	 * @param object
	 * @return
	 * @throws ConfigOptionException
	 */
	public static String getAdapterConfigString(Object object) throws ConfigOptionException{
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
		Map<String, Object> configOptionMap = new HashMap<String, Object>();
		List<Field> configFields = getConfigOptions(object);
		for (Field field : configFields) {
			try {
				Object fieldValue = ObjectConfigurator.getObjectProperty(object, field.getName());
				configOptionMap.put(field.getName(), fieldValue);
			} catch (ConfigException e) {
				throw new ConfigOptionException(e.getMessage());
			}
		}
		return configOptionMap;
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 */
	private static List<Field> getConfigOptions(Object object) {
		List<Field> configFields = new ArrayList<Field>();
		for(Field field : object.getClass().getDeclaredFields()){
			if (isConfigOption(field)) {
				configFields.add(field);
			}
		}
		for(Field field : object.getClass().getDeclaredFields()){
			if (isConfigOption(field)) {
				configFields.add(field);
			}
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
