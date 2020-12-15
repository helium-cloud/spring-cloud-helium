package org.helium.framework.spring.service;

public interface LicService {
	void addLicense(String lic);
	String getLicense();
	boolean checkLicense();
	boolean deleteLicense();
}
