package test.org.helium.boot.center.zk.servicesetter.api;

import org.helium.framework.annotations.ServiceInterface;

@ServiceInterface(id = "simple:SimpleConsumerService")
public interface SimpleConsumerService {

	void doWork(SimpleConsumerArgs simpleConsumerArgs);
}
