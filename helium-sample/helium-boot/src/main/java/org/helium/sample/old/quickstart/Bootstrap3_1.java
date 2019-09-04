package org.helium.sample.bootstrap.quickstart;

import org.helium.framework.spi.Bootstrap;

/**
 * Quickstart教程启动器: 参考1.10章节
 * Created by Coral on 6/15/17.
 */
public class Bootstrap3_1 {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample");
		Bootstrap.INSTANCE.addPath("helium-sample/build/resources/main/config"); // 将config目录加载到配置路径中
		Bootstrap.INSTANCE.initialize("bootstrap-3-1.xml", true, false);
	    Bootstrap.INSTANCE.run();	
	}
}
