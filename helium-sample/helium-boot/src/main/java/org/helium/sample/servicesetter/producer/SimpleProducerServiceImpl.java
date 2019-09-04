package org.helium.sample.servicesetter.producer;

import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.annotations.ServiceSetter;
import org.helium.sample.servicesetter.api.SimpleConsumerArgs;
import org.helium.sample.servicesetter.api.SimpleConsumerService;
import org.helium.sample.servicesetter.api.SimpleProducerService;
import org.helium.sample.servicesetter.api.SimpleProducerrArgs;


@ServiceImplementation
public class SimpleProducerServiceImpl implements SimpleProducerService {

	@ServiceSetter
	private SimpleConsumerService simpleConsumerService;
	@Override
	public void doWork(SimpleProducerrArgs simpleProducerrArgs) {
		try {
			System.out.println("simpleProducerrArgs:{}" + simpleProducerrArgs.toJsonObject());
			SimpleConsumerArgs simpleConsumerArgs = new SimpleConsumerArgs();
			simpleConsumerArgs.setMobile(simpleProducerrArgs.getMobile());
			simpleConsumerService.doWork(simpleConsumerArgs);
		} catch (Exception e){
			e.printStackTrace();
		}

	}
}
