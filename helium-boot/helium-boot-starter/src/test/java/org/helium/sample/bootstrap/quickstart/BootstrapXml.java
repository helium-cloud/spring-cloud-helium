package org.helium.sample.bootstrap.quickstart;


import org.helium.framework.spi.Bootstrap;
import org.helium.sample.bootstrap.quickstart.common.MessageRequest;
import org.helium.sample.bootstrap.quickstart.service.SimpleService;

/**
 * Quickstart教程启动器，参照1.4章节
 * Created by Coral on 6/15/17.
 */
public class BootstrapXml {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample");
		Bootstrap.INSTANCE.initialize("bootstrap.xml", true, false);

		testService();
	}

	public static void testService(){
		SimpleService simpleService = (SimpleService) Bootstrap.INSTANCE.getBean("simple:SimpleService").getBean();
		MessageRequest messageRequest = new MessageRequest();
		messageRequest.setMobile("13601030000");
		messageRequest.setType("message");
		messageRequest.setPriority(-1);
		simpleService.send(messageRequest);
	}
}
