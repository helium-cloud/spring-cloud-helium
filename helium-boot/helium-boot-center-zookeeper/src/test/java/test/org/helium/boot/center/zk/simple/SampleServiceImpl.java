package test.org.helium.boot.center.zk.simple;

import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.tag.Initializer;
import org.helium.http.webservice.WebServiceImplementation;


import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created by Coral on 8/23/15.
 */
@ServiceImplementation
@WebService(name = "AddService")
@WebServiceImplementation("/sample/hello")
public class SampleServiceImpl implements SampleService {
	@Override
	@WebMethod(operationName = "SayHello")
	public String sayHello(@WebParam(name = "Name") String name) {
		return "Bonjour:" + name;
	}

	@Override
	@WebMethod(operationName = "Add")
	public int add(@WebParam(name = "X")int x, @WebParam(name = "Y")int y) {
		return x + y;
	}

	@Override
	@WebMethod(operationName = "Sum")
	public int sum(@WebParam(name = "Args")int... args) {
		int s = 0;
		for (int i = 0; i < args.length; i++) {
			s += args[i];
		}
		return s;
	}

	@Override
	public void error() {
		throw new RuntimeException("Test Exception");
	}


	@Initializer
	public void start() {

	}
//
//	@Override
//	public Future<String> wait(int waitFor, String echo) {
//		try {
//			Thread.sleep(waitFor);
//			return Future.createSuccess("Done:" + echo);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}
