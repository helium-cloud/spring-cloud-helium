package test.org.helium.boot.center.zk.servicesetter.consumer;

import org.helium.framework.spi.Bootstrap;

/**
 * 消费者测试
 */
public class BootstrapConsumerTest {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-boot/helium-boot-center-zookeeper/src/test/java/test/org/helium/boot/center/zk/servicesetter/consumer");
		Bootstrap.INSTANCE.initialize("bootstrap-consumer.xml", true, false);

	}
}
