package org.helium.dtask.tester;

import org.helium.framework.spi.Bootstrap;

/**
 * Created by Coral on 3/19/16.
 */
public class WorkerBootstrap {
	public static void main(String[] args) {
		try {
			Bootstrap.INSTANCE.addPath("helium-dashboard/build/resources/test");
			Bootstrap.INSTANCE.addPath("helium-dashboard/build/resources/main/META-INF");
			Bootstrap.INSTANCE.initialize("bootstrap-2163-worker.xml", true, true);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}
}
