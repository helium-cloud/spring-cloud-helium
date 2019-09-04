package org.helium.sample.bootstrap.quickstart;

import org.helium.framework.spi.Bootstrap;

/**
 * Quickstart教程启动器: 参考1.13章节
 * Created by Coral on 7/11/17.
 */
public class Bootstrap4 {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample");
		Bootstrap.INSTANCE.addPath("helium-sample/build/resources/main/config"); // 将config目录加载到配置路径中
		Bootstrap.INSTANCE.addPath("helium-sample/build/libs"); // 将helium-sample~.jar的目录加入配置路径中
		Bootstrap.INSTANCE.addPath("helium-dashboard/build/libs"); // 将helium-dashboard~.jar的目录加入配置路径中
		Bootstrap.INSTANCE.initialize("bootstrap-4.xml", true, false);
		Bootstrap.INSTANCE.run();
	}
}
