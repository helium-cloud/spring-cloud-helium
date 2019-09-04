package org.helium.database.test;

import org.helium.framework.annotations.ServiceInterface;

/**
 * Created by Coral on 10/12/15.
 */
@ServiceInterface(id = "test:SampleService")
public interface SampleService {
	void foo();

	long foo2();
}
