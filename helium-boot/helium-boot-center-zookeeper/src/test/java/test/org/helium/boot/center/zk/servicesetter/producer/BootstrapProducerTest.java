package test.org.helium.boot.center.zk.servicesetter.producer;

import org.helium.framework.BeanContext;
import org.helium.framework.spi.Bootstrap;
import test.org.helium.boot.center.zk.servicesetter.api.SimpleProducerService;
import test.org.helium.boot.center.zk.servicesetter.api.SimpleProducerrArgs;


public class BootstrapProducerTest {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-boot/helium-boot-center-zookeeper/src/test/java/test/org/helium/boot/center/zk/servicesetter/producer");
		Bootstrap.INSTANCE.initialize("bootstrap-producer.xml", false, false);

		SimpleProducerService bean = Bootstrap.INSTANCE.getService(SimpleProducerService.class);
		for (int i = 0; i < 20; i++) {
			SimpleProducerrArgs simpleProducerrArgs = new SimpleProducerrArgs();
			simpleProducerrArgs.setMobile("1360103000" + i % 5);
			simpleProducerrArgs.setType("cloud" + i);
			simpleProducerrArgs.setPriority(-1);
			bean.doWork(simpleProducerrArgs);

			Thread.sleep(10000);
		}
	}
}
