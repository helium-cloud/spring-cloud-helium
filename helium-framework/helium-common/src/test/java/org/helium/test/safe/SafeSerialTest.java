package org.helium.test.safe;

import org.helium.safe.SafeSerial;
import org.junit.Test;

public class SafeSerialTest {
	@Test
	public void sTest(){
		System.out.println(SafeSerial.getCPUSerial());;
		System.out.println(SafeSerial.getMACAddressByLinux());;
	}
}
