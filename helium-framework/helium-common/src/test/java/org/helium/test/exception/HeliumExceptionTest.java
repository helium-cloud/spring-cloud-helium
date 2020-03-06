package org.helium.test.exception;

import org.helium.exception.HeliumException;
import org.junit.Test;

public class HeliumExceptionTest {
	@Test
	public void TestException(){
		HeliumException heliumException = new HeliumException(1, "normal exception");
		System.out.println(heliumException);
	}
}
