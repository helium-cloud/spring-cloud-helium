package com.feinno.urcs.data.redis.test.client;


import org.helium.framework.spi.Bootstrap;
import org.helium.framework.test.ServiceForTest;

/**
 * Created by Leon on 8/5/16.
 */
public class RedisBootstrapTest {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-data-redis/src/test/java/com/feinno/urcs/data/redis/test/client/resources");
		Bootstrap.INSTANCE.initialize("bootstrap-redis.xml", true, false);
		System.out.println("RedisBootstrapTest Success");
		ServiceForTest serviceForTest = (ServiceForTest) Bootstrap.INSTANCE.getBean("test:ServiceForTest").getBean();
		serviceForTest.test();

	}
}
