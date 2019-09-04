package test.org.helium.boot.center.zk.servicesetter.consumer;

import org.helium.framework.annotations.ServiceImplementation;
import test.org.helium.boot.center.zk.servicesetter.api.SimpleConsumerArgs;
import test.org.helium.boot.center.zk.servicesetter.api.SimpleConsumerService;


@ServiceImplementation
public class SimpleConsumerServiceImpl implements SimpleConsumerService {

	@Override
	public void doWork(SimpleConsumerArgs simpleConsumerArgs) {
		System.out.println("simpleConsumerArgs:{}" + simpleConsumerArgs.toJsonObject());
	}
}
