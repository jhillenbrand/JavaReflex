package pojoxml;

import org.junit.Test;

import net.sytes.botg.pojoxml.PoJoXML;

public class UnitTest_PoJoXML {

	@Test
	public void test00() {
		
		Person p = new Person();
		
		System.out.println(PoJoXML.toXML(p));
		
	}
}
