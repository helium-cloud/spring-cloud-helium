org.helium.rpc.channel 包基础定义以及输出接口说明
===============================================================================

对org.helium.rpc.channel.xxx实现输出接口
-------------------------------------------------------------------------------
* RpcChannel, RpcChannelClientHandler, RpcChannelServerHandler
* RpcEventListener
	+ connectionCreated()
	+ transactionCreated()
	+ connectionCreated()
	+ transactionDestroyed()
* RpcConnectionController
	+ 
* RpcConnectionlessConnection

对org.helium.rpc.client输出接口
-------------------------------------------------------------------------------
* Simplex
	+ conn = RpcChannel.getConnection(ep)	// connection可以缓存, 无连接的返回
	+ tx = RpcChannel.newTransaction(conn)	// 根据connection的reusable特性实现复用
	+ future = conn.send(tx)				// 实现真正的发送并返回异步对象
	+ ? 如何处理连接未建立时的并发逻辑
	+ 如何处理Simplex的逻辑
* Duplex
	+ conn = RpcChannel.getConnection(ep)
	+ ConnectFuture future = conn.connect()
	+ conn.setContoller(controller)			// 设置连接上的监听器
	+ future = conn.send						

对org.helium.rpc.server输出接口
-------------------------------------------------------------------------------
* Simplex
	+ ServerContoller controller...			//  	
	+ channel.setController(controller)		// 