package test.org.helium.boot.center.zk.servicesetter.api;

import org.helium.framework.annotations.ServiceInterface;

@ServiceInterface(id = "simple:SimpleProducerService")
public interface SimpleProducerService {
	void doWork(SimpleProducerrArgs simpleProducerrArgs);
}
