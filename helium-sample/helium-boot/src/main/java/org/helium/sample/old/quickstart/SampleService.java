package org.helium.sample.bootstrap.quickstart;

import org.helium.framework.annotations.ServiceInterface;

/**
 * quickstart实例2：Service注入
 * Created by Coral on 6/15/17.
 */
@ServiceInterface(id = "quickstart:SampleService")
public interface SampleService {
	SampleUser getUser(int userId) throws Exception;
	
	void insertUser(SampleUser user) throws Exception;
}
