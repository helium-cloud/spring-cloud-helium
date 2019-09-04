package org.helium.sample.bootstrap.quickstart;

import org.helium.framework.spi.Bootstrap;

/**
 * Created by Coral on 7/7/15.
 */
public class SampleTestMain {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample/build/resources/main/");
		Bootstrap.INSTANCE.initialize("bootstrap.xml");
		while (true) {
			Thread.sleep(100);
		}
		// Bundle bundle;
	}
}
