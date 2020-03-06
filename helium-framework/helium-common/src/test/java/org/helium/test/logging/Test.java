package org.helium.test.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @author coral
 * @version 创建时间：2014年9月17日
 * 类说明
 */
public class Test {
	private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

	public static void main(String args[]) {
		// FilterManager.addMarker(MarkerFactory.getMarker("SystemLog"));

		LOGGER.info("Hello1");

		Marker marker = MarkerFactory.getMarker("SystemLog");

		LOGGER.info(marker, "Hello2");
		LOGGER.info(marker, "Hello3");
		LOGGER.info("Hello4");
		LOGGER.info("Hello5");
		LOGGER.info("Hello6");
		LOGGER.info("Hello7");


		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("End...");
		System.exit(1);
	}
}
