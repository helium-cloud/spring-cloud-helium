package org.helium.sample.future;

import org.helium.threading.Future;
import org.helium.framework.annotations.ServiceInterface;
import org.helium.sample.future.common.MessageRequest;
import org.helium.sample.future.common.MessageResponse;


/**
 * 测试接口性能
 */
@ServiceInterface(id = "simple:FutureService")
public interface FutureService {
	MessageResponse adapterNormal(MessageRequest messageRequest);

	Future<MessageResponse> adapterFuture(MessageRequest messageRequest);

	MessageResponse adapterInnerFuture(MessageRequest messageRequest);
}
