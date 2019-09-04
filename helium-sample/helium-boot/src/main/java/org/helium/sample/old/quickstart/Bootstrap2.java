package org.helium.sample.bootstrap.quickstart;

import org.helium.framework.spi.Bootstrap;

/**
 * Quickstart教程启动器，参照1.8章节
 * Created by Coral on 6/15/17.
 */
public class Bootstrap2 {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample");
		Bootstrap.INSTANCE.addPath("helium-sample/src/main/resources/config"); // 将config目录加载到配置路径中
		Bootstrap.INSTANCE.initialize("bootstrap-2.xml", true, false);
	    Bootstrap.INSTANCE.run();
	}
}
