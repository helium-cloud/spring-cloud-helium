package org.helium.sample.bootstrap.quickstart;

import org.helium.framework.spi.Bootstrap;

/**
 * Quickstart教程启动器，参照1.4章节
 * Created by Coral on 6/15/17.
 */
public class Bootstrap1 {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample");
		Bootstrap.INSTANCE.initialize("bootstrap-1.xml", true, false);
	    Bootstrap.INSTANCE.run();	
	}
}
