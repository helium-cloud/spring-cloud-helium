package org.helium.rpc.client
=================================================================

功能列表
-----------------------------------------------------------------
# 客户端代理 RpcMethodStub
# 软负载功能, 功能附加在RpcEndpoint类上
	+ RpcProxyFactory.getMethodStub(endpoint);
# 负载连接的轮询与替换功能

Classes
-----------------------------------------------------------------
# class RpcProxyFactory
# class RpcMethodStub
	-> handler.createTransaction()
		DirectHandler
			provider.getConnectionSink()
		NLBHandler
		RollingHandler
	+ org.helium.rpc.channel
	-> RpcClientConnectionProvider.getConnectionSink()
	-> clientConnectionSink.createTransaction()	
	
# class RpcClientTrasnactoinHandler
	<- RpcClientDirect
	<- RpcClientNLB ... List<RpcClientDirect>
	<- RpcClientRolling ... client RpcClientDirect
	
-----------------------------------------------------------------
RpcMethodStub
	ep.getConnection();
	RpcClientTransactionHandler
		handler->createTransaction();
		handler->getSink()
		sink->createTransaction()
		
		



