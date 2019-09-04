package test.org.helium.boot.center.zk.simple;

import org.helium.framework.annotations.ServiceInterface;
import org.helium.stack.rpc.LegacyRpcServiceInterface;

/**
 * Created by Coral on 7/31/15.
 */
@ServiceInterface(id = "test:SampleRpcServiceInterface")
@LegacyRpcServiceInterface
public interface SampleRpcServiceInterface {
	void foo(String bar);
}
