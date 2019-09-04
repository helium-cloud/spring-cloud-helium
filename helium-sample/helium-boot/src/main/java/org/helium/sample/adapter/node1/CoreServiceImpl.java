package org.helium.sample.adapter.node1;

import org.helium.framework.annotations.AdapterTag;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.sample.adapter.common.CoreService;
import org.helium.sample.adapter.common.MessageArgs;

/**
 * Created by Coral on 9/10/16.
 */
@AdapterTag(name ="grayrouter")
@ServiceImplementation
public class CoreServiceImpl implements CoreService {

	@Override
	public void adapter(MessageArgs messageArgs) {
		System.out.println("adapter:{}" + messageArgs.toJsonObject());
	}
}
