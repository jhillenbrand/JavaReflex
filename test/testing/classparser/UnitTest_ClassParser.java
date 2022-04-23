package testing.classparser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sytes.botg.reflection.JFlex;

public class UnitTest_ClassParser {
	
	@Test
	public void testIsPrimitive1() {
		
		assertEquals(true, JFlex.isPrimitive(double.class, true));
				
	}
	
	@Test
	public void testIsPrimitive2() {

		assertEquals(true, JFlex.isPrimitive(Double.class, true));
		
	}
	
	@Test
	public void testIsPrimitive3() {

		assertEquals(true, JFlex.isPrimitive(String.class, true));
		
	}
	
	@Test
	public void testSuperClass01() {
		
		assertEquals(false, JFlex.hasSuperClass(Integer.class, Integer.class));		
		
	}	
	
	@Test
	public void testSuperClass02() {
		
		assertEquals(true, JFlex.hasSuperClass(Integer.class, Object.class));		
		
	}
}
