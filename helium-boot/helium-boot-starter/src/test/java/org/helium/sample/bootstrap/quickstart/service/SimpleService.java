package org.helium.sample.bootstrap.quickstart.service;

import org.helium.framework.annotations.ServiceInterface;
import org.helium.sample.bootstrap.quickstart.common.MessageRequest;
import org.helium.sample.bootstrap.quickstart.common.MessageResponse;
import org.helium.threading.Future;


/**
 * 测试接口性能
 */
@ServiceInterface(id = "simple:SimpleService")
public interface SimpleService {

	MessageResponse send(MessageRequest messageRequest);
}
