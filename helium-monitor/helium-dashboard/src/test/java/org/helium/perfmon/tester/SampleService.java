package org.helium.perfmon.tester;

import org.helium.framework.annotations.ServiceInterface;

/**
 * Created by Coral on 10/28/15.
 */
@ServiceInterface(id = "test:SampleService")
public interface SampleService {
	void testDedicatedTask(long i);
}
