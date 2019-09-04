package org.helium.sample.servicesetter.api;

import org.helium.framework.annotations.ServiceInterface;

@ServiceInterface(id = "simple:SimpleConsumerService")
public interface SimpleConsumerService {

	void doWork(SimpleConsumerArgs simpleConsumerArgs);
}
