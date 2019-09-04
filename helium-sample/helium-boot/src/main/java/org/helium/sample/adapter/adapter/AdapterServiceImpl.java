package org.helium.sample.adapter.adapter;

import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.service.ServiceMatchResults;
import org.helium.framework.service.ServiceRouter;
import org.helium.framework.spi.Bootstrap;
import org.helium.framework.tag.Initializer;
import org.helium.sample.adapter.common.AdapterModuleContext;
import org.helium.sample.adapter.common.CoreService;
import org.helium.sample.adapter.common.MessageArgs;

import java.util.List;

/**
 * Created by Coral on 9/10/16.
 */
@ServiceImplementation
public class AdapterServiceImpl implements AdapterService {
	private String adapterTag = "grayrouter";
	private ServiceRouter serviceRouter;

	@Initializer
	private void init(){

	}

	@Override
	public void adapter(MessageArgs messageArgs) {
		if (serviceRouter == null){
			synchronized (this){
				serviceRouter = new ServiceRouter( Bootstrap.INSTANCE, adapterTag, null);
			}

		}

		System.out.println("adapter:{}" + messageArgs.toJsonObject());
		AdapterModuleContext adapterModuleContext = new AdapterModuleContext();
		adapterModuleContext.putModuleData("mobile", messageArgs.getMobile());
		try {
			ServiceMatchResults<CoreService> serviceProxy = serviceRouter.match(adapterModuleContext, messageArgs.getMobile(), -1, messageArgs);

			List<CoreService> coreServiceList = serviceProxy.getServices();
			System.out.println("process service :{}" + serviceProxy.toString());
			for (CoreService coreService:coreServiceList) {
				coreService.adapter(messageArgs);
			}
		} catch (Exception e){
			e.printStackTrace();
		}

	}
}
