###接口性能测试

```java
package org.helium.sample.future;

import Future;
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
```

####1.直接单条调用
```
server:64 

client:500

future:1w5
//
MessageResponse adapterNormal(MessageRequest messageRequest);
Future<MessageResponse> adapterFuture(MessageRequest messageRequest);
MessageResponse adapterInnerFuture(MessageRequest messageRequest);


```


####2.单条内部转批量调用
####3.直接异步处理