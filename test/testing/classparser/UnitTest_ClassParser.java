package testing.classparser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sytes.botg.reflection.ClassParser;

public class UnitTest_ClassParser {
	
	@Test
	public void testIsPrimitive1() {
		
		assertEquals(true, ClassParser.isPrimitive(double.class, true));
				
	}
	
	@Test
	public void testIsPrimitive2() {

		assertEquals(true, ClassParser.isPrimitive(Double.class, true));
		
	}
	
	@Test
	public void testIsPrimitive3() {

		assertEquals(true, ClassParser.isPrimitive(String.class, true));
		
	}
	
}
