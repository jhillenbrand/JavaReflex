package pojoxml;

import java.util.LinkedHashMap;
import java.util.Map;

public class Person {
	private String name = "Anton";
	private String bday = "02.09.2011";
	private Address address = new Address();
	private String[] siblings = {"Chris", "John", "Paula"};
	private Map<String, String> uncles = new LinkedHashMap<String, String>(){{put("Chris", "Chris"); put("John", "John");}};
}
