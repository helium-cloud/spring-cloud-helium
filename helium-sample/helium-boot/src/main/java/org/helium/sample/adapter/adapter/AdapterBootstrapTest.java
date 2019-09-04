package org.helium.sample.adapter.adapter;

import org.helium.framework.BeanContext;
import org.helium.framework.spi.Bootstrap;
import org.helium.sample.adapter.common.MessageArgs;


/**
 * Created by Coral on 9/10/16.
 */
public class AdapterBootstrapTest {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample/src/main/java/org/helium/sample/adapter/adapter");
		Bootstrap.INSTANCE.initialize("bootstrap-adapter1.xml", false, false);

		AdapterService bean = BeanContext.getContextService().getService(AdapterService.class);

		MessageArgs messageArgs1 = new MessageArgs();
		messageArgs1.setMobile("13601030000");
		messageArgs1.setType("cloud");
		messageArgs1.setPriority(-1);
		bean.adapter(messageArgs1);

		for (int i = 0; i < 20; i++) {
			MessageArgs messageArgs = new MessageArgs();
			messageArgs.setMobile("1360103000" + i % 5);
			messageArgs.setType("cloud" + i);
			messageArgs.setPriority(-1);
			bean.adapter(messageArgs);
		}

	}
}
