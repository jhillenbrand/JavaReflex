package net.sytes.botg.pojoxml;

import java.lang.reflect.Field;

public class PoJoUtil {
	
	/**
	 * returns the String n times
	 * @param n
	 * @param s
	 * @return
	 */
	public static String repeat(int n, String s) {
		return new String(new  char[n]).replace("\0", s);
	}
	
	public static boolean isIgnorePoJoXMLField(Field field) {
		IgnorePoJoXML annot = field.getAnnotation(IgnorePoJoXML.class);
		if (annot == null) {
			return false;
		} else {
			return true;
		}
	}
	
}
