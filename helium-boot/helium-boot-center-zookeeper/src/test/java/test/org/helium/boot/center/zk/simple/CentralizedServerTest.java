package test.org.helium.boot.center.zk.simple;

import org.helium.framework.servlet.ServletRouter;
import org.helium.framework.spi.Bootstrap;

/**
 * Created by Coral on 8/7/15.
 */
public class CentralizedServerTest {
	public static void main(String[] args) throws Exception {

		Bootstrap.INSTANCE.addPath("helium-http/build/resources/test/META-INF");
		Bootstrap.INSTANCE.initialize("bootstrap-center.xml");

		ServletRouter router = new ServletRouter("http", Bootstrap.INSTANCE);

//		ServletMatchResults results = router.match(null, ServletMatchResult.ALL_FILTER, null);
//		System.out.println("results:" + results.hasResult());
//		System.out.println("results:" + results.getServletEndpoint());

		SampleService service = Bootstrap.INSTANCE.getService(SampleService.class);
		System.out.println("hello:" + service.sayHello("foo"));

		while (true) {
			Thread.sleep(100);
		}
	}
}
