//package org.helium.database.test;
//
//import org.helium.framework.spi.Bootstrap;
//import org.helium.framework.test.ServiceForTest;
//
///**
// * Created by Coral on 10/12/15.
// */
//public class DatabaseBootstrapTest {
//	public static void main(String[] args) throws Exception {
//		Bootstrap.INSTANCE.addPath("helium-data-base/build/resources/test");
//		Bootstrap.INSTANCE.initialize("bootstrap-database.xml", true, false);
//
//		SampleService service = Bootstrap.INSTANCE.getService(SampleService.class);
//		System.out.println("id:" + service.foo2());
//		service.foo();
//
//		ServiceForTest test = Bootstrap.INSTANCE.getService(ServiceForTest.class);
//		test.test();
//
//		System.out.println("OVER----------------------");
//	}
//}
