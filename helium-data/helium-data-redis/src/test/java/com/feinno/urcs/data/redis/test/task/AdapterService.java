package com.feinno.urcs.data.redis.test.task;

import org.helium.framework.annotations.ServiceInterface;


/**
 * Created by Leon on 9/10/16.
 */
@ServiceInterface(id = "simple:AdapterService")
public interface AdapterService{
	void adapter(AdapterTaskArgs messageArgs);
}
