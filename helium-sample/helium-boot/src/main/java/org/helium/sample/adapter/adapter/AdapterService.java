package org.helium.sample.adapter.adapter;

import org.helium.framework.annotations.ServiceInterface;
import org.helium.sample.adapter.common.MessageArgs;


/**
 * Created by Coral on 9/10/16.
 */
@ServiceInterface(id = "simple:AdapterService")
public interface AdapterService{
	void adapter(MessageArgs messageArgs);
}
