package wniemiec.component.java.testfiles;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleTest {

	@Test
	public void testMultip() {
		int x1 = 10;
		int x2 = 20;
		int res = 0;
		
		for (int i = 0; i < x2; i++) {
			res += x1;
		}
		
		assertTrue(res == 200);
	}
}
