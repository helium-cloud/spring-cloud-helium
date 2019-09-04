package org.helium.sample.adapter.common;

import org.helium.framework.annotations.ServiceInterface;

/**
 * Created by Coral on 9/10/16.
 */

@ServiceInterface(id = "simple:CoreService")
public interface CoreService {
	void adapter(MessageArgs messageArgs);
}
