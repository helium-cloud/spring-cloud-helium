package org.helium.sample.servicesetter.consumer;

import org.helium.framework.annotations.ServiceImplementation;
import org.helium.sample.servicesetter.api.SimpleConsumerArgs;
import org.helium.sample.servicesetter.api.SimpleConsumerService;


@ServiceImplementation
public class SimpleConsumerServiceImpl implements SimpleConsumerService {

	@Override
	public void doWork(SimpleConsumerArgs simpleConsumerArgs) {
		System.out.println("simpleConsumerArgs:{}" + simpleConsumerArgs.toJsonObject());
	}
}
