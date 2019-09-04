package test.org.helium.boot.center.zk.simple;

import org.helium.framework.annotations.ServiceImplementation;
import org.helium.stack.rpc.LegacyRpcService;


/**
 * Created by Coral on 7/31/15.
 */
@ServiceImplementation
@LegacyRpcService(serviceName = "haha")
public class SampleRpcService implements SampleRpcServiceInterface {
	@Override
	public void foo(String bar) {

	}
}
