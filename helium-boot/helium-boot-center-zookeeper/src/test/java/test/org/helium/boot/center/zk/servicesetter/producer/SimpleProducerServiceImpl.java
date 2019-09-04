package test.org.helium.boot.center.zk.servicesetter.producer;

import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.annotations.ServiceSetter;
import test.org.helium.boot.center.zk.servicesetter.api.SimpleConsumerArgs;
import test.org.helium.boot.center.zk.servicesetter.api.SimpleConsumerService;
import test.org.helium.boot.center.zk.servicesetter.api.SimpleProducerService;
import test.org.helium.boot.center.zk.servicesetter.api.SimpleProducerrArgs;


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
