package org.helium.test.superpojo.xml;

import java.util.List;

public class TestRead {
	public static void main(String args[]) {
		StaXParser read = new StaXParser();
		List<Item> readConfig = read.readConfig("/home/lvmingwei/Dev/git/superpojo/src/superpojo-test/src/config.xml");
		for (Item item : readConfig) {
			System.out.println(item);
		}
	}
}
