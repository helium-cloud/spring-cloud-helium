package org.helium.sample.adapter.node1;

import org.helium.framework.spi.Bootstrap;

/**
 * Created by Coral on 9/10/16.
 */
public class CoreBootstrapNode1Test {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample/src/main/java/org/helium/sample/adapter/producer");
		Bootstrap.INSTANCE.initialize("bootstrap-core.xml");

	}
}
