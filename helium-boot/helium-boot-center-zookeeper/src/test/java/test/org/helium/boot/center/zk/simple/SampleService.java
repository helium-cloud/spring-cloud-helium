package test.org.helium.boot.center.zk.simple;

import org.helium.framework.annotations.ServiceInterface;

/**
 * Created by Coral on 8/23/15.
 */
@ServiceInterface(id = "foo:SampleService")
public interface SampleService {
	String sayHello(String name);

	int add(int x, int y);

	int sum(int... args);

	void error();

	// Future<String> wait(int waitFor, String echo);
}
