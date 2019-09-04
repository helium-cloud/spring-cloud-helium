package org.helium.sample.task;

import org.helium.framework.spi.Bootstrap;


/**
 * Created by Coral on 9/10/16.
 */
public class AdapterBootstrapTest {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample/src/main/java/org/helium/sample/task");
		Bootstrap.INSTANCE.addPath("helium-sample/src/main/java/org/helium/sample/task/resources");
		Bootstrap.INSTANCE.initialize("bootstrap-adapter.xml");

	}
}
