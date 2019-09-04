package org.helium.sample.future;

import org.helium.framework.spi.Bootstrap;


/**
 * Created by Coral on 9/10/16.
 */
public class FutureBootstrapTest {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample/src/main/java/org/helium/sample/future");
		Bootstrap.INSTANCE.initialize("bootstrap-future.xml", false, false);

	}
}
