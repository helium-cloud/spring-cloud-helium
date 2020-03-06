package org.helium.test.superpojo.xml;

public class TestWrite {

	public static void main(String[] args) {
		StaxWriter configFile = new StaxWriter();
		configFile.setFile("/home/lvmingwei/Dev/git/superpojo/src/superpojo-test/src/config2.xml");
		try {
			configFile.saveConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
