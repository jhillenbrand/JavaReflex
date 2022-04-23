package net.sytes.botg.reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassFactory {

	private static final Logger logger = LoggerFactory.getLogger(ClassFactory.class);
	
	public static Map<String, Object> makeAll(String[] fullNameOfClasses){
		List<Class<?>> clazzes = new ArrayList<Class<?>>();
		for (String fullNameOfClass : fullNameOfClasses) {
			Class<?> clazz = JFlex.parseString2Class(fullNameOfClass);
			if (clazz != null) {
				clazzes.add(clazz);
			}
		}
		return makeAll((Class<?>[]) clazzes.toArray());
	}
	
	public static Map<String, Object> makeAll(String fullNameOfClass){
		Class<?> clazz = JFlex.parseString2Class(fullNameOfClass);
		return makeAll(clazz);
	}
	
	/**
	 * create a Map of all available Class instances based on classToLoad
	 * @param classToLoad
	 * @return
	 */
	public static Map<String, Object> makeAll(Class<?> classToLoad){
		return makeAll(new Class<?>[]{ classToLoad });
	}
	
	/**
	 * create a Map of all available Class Instances based classesToLoad that can be found in src/META-INF/services
	 * @param <E>
	 * @return
	 */
	public static Map<String, Object> makeAll(Class<?>[] classesToLoad){
		Map<String, Object> instances = new HashMap<String, Object>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				
		// try instantiating classes
		for (Class<?> clazz : classesToLoad) {
			if (clazz != null) {
				ServiceLoader<Object> serviceLoader = (ServiceLoader<Object>) ServiceLoader.load(clazz, classLoader);
				Iterator<Object> iterator = serviceLoader.iterator();
				while(iterator.hasNext()) {			
					try {			
						Object instance = iterator.next();
						instances.put(instance.getClass().getSimpleName(), instance);
					} catch (ServiceConfigurationError e) {
						logger.trace("Could not instantiate object of " + clazz.getSimpleName(), e);
					}			
				}
			}
		}
		return instances;
	}
	
}
