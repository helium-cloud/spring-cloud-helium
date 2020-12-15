package org.helium.framework.spring.utils;

import org.helium.safe.SafeSerial;

public class LicenseGen {
	public static String LICENSE_KEY = "djadiKJdj49dFJLd";
	public static String getLicense(){
		return getLicense(LICENSE_KEY);
	}
	public static String getLicense(String key){
		try {
			return AESUtil.encryptAES(SafeSerial.getCPUSerial(), key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
