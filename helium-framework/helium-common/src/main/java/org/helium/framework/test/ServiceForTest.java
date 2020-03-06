package org.helium.framework.test;

import org.helium.framework.annotations.ServiceInterface;

/**
 * Created by Coral on 8/3/16.
 */
@ServiceInterface(id = "test:ServiceForTest")
public interface ServiceForTest {
	void test() throws Exception;
}
