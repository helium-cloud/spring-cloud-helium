package org.helium.http.test;

import org.helium.framework.spi.Bootstrap;

/**
 * Created by Coral on 7/23/15.
 */
public class HttpBootstrapSample {
	public static void main(String[] args) throws InterruptedException {
		try {
			Bootstrap.INSTANCE.addPath("helium-http/build/resources/test/META-INF");
			Bootstrap.INSTANCE.addPath("helium-http/build/resources/test/");
			Bootstrap.INSTANCE.addPath("../../ngcc-v2/config_test");
			Bootstrap.INSTANCE.initialize("bootstrap.xml");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}

		Thread.sleep(30);
		System.out.println("hello:" + TestConfigurator.INSTANCE.getHello());
		System.out.println("hello2:" + TestConfigurator.INSTANCE.getHello2());

		while (true) {
			Thread.sleep(10);
		}
	}
}
