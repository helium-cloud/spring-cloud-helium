package org.helium.sample.boot.license;

import org.helium.framework.spring.utils.LicenseGen;

public class LicenseGenTest {
	public static void main(String[] args) {
		System.out.println(LicenseGen.getLicense("njadiKJdj49dFJLd"));
		System.out.println(LicenseGen.getLicense());
	}
}
