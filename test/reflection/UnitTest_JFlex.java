package reflection;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.sytes.botg.datatypes.ValueInterface;
import net.sytes.botg.datatypes.buffers.TimedBuffer;
import net.sytes.botg.reflection.JFlex;

public class UnitTest_JFlex {

	@Test
	public void test000() throws IOException, ClassNotFoundException {
		List<Class> clazzes = JFlex.findClassesInPackage("net.sytes.botg.reflection");		
		System.out.println(clazzes);
	}
	
	@Test
	public void test010() throws ClassNotFoundException, IOException {
		List<Class> clazzes = JFlex.findClassesInPackage("");
		
		System.out.println(clazzes);
	}
	
	@Test
	public void test020() throws ClassNotFoundException, IOException {		
		List<Class> clazzes = JFlex.findClassesWithParentClass(TimedBuffer.class);		
		System.out.println(clazzes);		
	}
	
	@Test
	public void test030() throws ClassNotFoundException, IOException {		
		List<Class> clazzes = JFlex.findClassesWithInterface(ValueInterface.class);		
		System.out.println(clazzes);		
	}
	
	@Test
	public void test040() throws ClassNotFoundException, IOException {		
		List<Class> clazzes = JFlex.findClassesWithInterface(ValueInterface.class);	
		Map<String, Object> instances = JFlex.makeAll(clazzes);
		System.out.println(instances);		
	}
	
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
