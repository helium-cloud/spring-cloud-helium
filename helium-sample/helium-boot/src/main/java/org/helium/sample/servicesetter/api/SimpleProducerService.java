package org.helium.sample.servicesetter.api;

import org.helium.framework.annotations.ServiceInterface;

@ServiceInterface(id = "simple:SimpleProducerService")
public interface SimpleProducerService {
	void doWork(SimpleProducerrArgs simpleProducerrArgs);
}
