package org.helium.sample.servicesetter.consumer;

import org.helium.framework.spi.Bootstrap;

/**
 * 消费者测试
 */
public class BootstrapConsumerTest {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample/src/main/java/org/helium/sample/servicesetter/consumer");
		Bootstrap.INSTANCE.initialize("bootstrap-consumer.xml");

	}
}
