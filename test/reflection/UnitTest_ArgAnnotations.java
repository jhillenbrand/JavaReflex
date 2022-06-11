package reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

public class UnitTest_ArgAnnotations {

	@Test
	public void test000() {
		
		Method[] methods = this.getClass().getDeclaredMethods();
		for(Method method : methods) {
			
			Annotation[][] as = method.getParameterAnnotations();
			
			for (int i = 0; i < as.length; i++) {
				Annotation[] as2 = as[i];
				for (int j = 0; j < as2.length; j++) {
					Annotation a = as2[j];
					System.out.println(a.toString());
										
				}
				
			}
			
		}		
	}
	
	public void testMethod(@RESTParam int p1, @RESTParam String p2) {
		System.out.println(p1 + "; " + p2);
	}
	
}
