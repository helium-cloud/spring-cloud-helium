//package org.helium.sample.old.advanced;
//
//import org.helium.framework.spi.Bootstrap;
//
///**
// * 高级教程启动器
// * Created by Coral on 7/13/17.
// */
//public class BootstrapAdvanced {
//	public static void main(String[] args) throws Exception {
//		Bootstrap.INSTANCE.addPath("helium-sample");
//		Bootstrap.INSTANCE.addPath("helium-sample/build/resources/main/config"); // 将config目录加载到配置路径中
//		Bootstrap.INSTANCE.initialize("bootstrap-advanced.xml", true, false);
//		Bootstrap.INSTANCE.run();
//	}
//}
