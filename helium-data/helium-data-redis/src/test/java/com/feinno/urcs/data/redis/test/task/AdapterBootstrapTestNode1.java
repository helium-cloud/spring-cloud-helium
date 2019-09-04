package com.feinno.urcs.data.redis.test.task;

import org.helium.framework.spi.Bootstrap;


/**
 * Created by Leon on 9/10/16.
 */
public class AdapterBootstrapTestNode1 {
	public static void main(String[] args) throws Exception {

		Bootstrap.INSTANCE.addPath("helium-data-redis/src/test/java/com/feinno/urcs/data/redis/test/task");
		Bootstrap.INSTANCE.addPath("helium-data-redis/src/test/java/com/feinno/urcs/data/redis/test/task/resources");
		Bootstrap.INSTANCE.initialize("bootstrap-adapter1.xml");

	}
}
